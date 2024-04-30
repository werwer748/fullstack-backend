package org.hugo.apiserver.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hugo.apiserver.dto.PageRequestDto;
import org.hugo.apiserver.dto.PageResponseDto;
import org.hugo.apiserver.dto.ProductDto;
import org.hugo.apiserver.service.ProductService;
import org.hugo.apiserver.util.CustomFileUtil;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final CustomFileUtil fileUtil;
    private final ProductService productService;

//    @PostMapping("/")
//    public Map<String, String> register(ProductDto productDto) {
//        log.info("register: " + productDto);
//
//        List<MultipartFile> files = productDto.getFiles();
//
//        List<String> uploadFileNames = fileUtil.saveFiles(files);
//
//        productDto.setUploadFileNames(uploadFileNames);
//
//        log.info(uploadFileNames);
//
//        return Map.of("RESULT", "SUCCESS");
//    }

    @GetMapping("/view/{fileName}")
    public ResponseEntity<Resource> viewFileGet(@PathVariable("fileName") String fileName) {
        return fileUtil.getFile(fileName);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')") // 권한 체크
    @GetMapping("/list")
    public PageResponseDto<ProductDto> list(PageRequestDto pageRequestDto) {
        return productService.getList(pageRequestDto);
    }

    @PostMapping("/")
    public Map<String, Long> register(ProductDto productDto) {
        List<MultipartFile> files = productDto.getFiles();
        List<String> uploadFileNames = fileUtil.saveFiles(files);

        productDto.setUploadFileNames(uploadFileNames);

        log.info(uploadFileNames);

        Long pno = productService.register(productDto);

        // 업로드시 로딩 확인을 위해 추가
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return Map.of("result", pno);
    }

    @GetMapping("/{pno}")
    public ProductDto read(@PathVariable("pno") Long pno) {
        return productService.get(pno);
    }

    @PutMapping("/{pno}")
    public Map<String, String> modify(@PathVariable("pno") Long pno, ProductDto productDto) {
        productDto.setPno(pno);

        // db에 저장되어있는 상품 정보를 우선 가져온다.
        ProductDto oldProductDto = productService.get(pno);

        // 새로운 파일 업로드
        List<MultipartFile> files = productDto.getFiles();
        List<String> currentUploadFileNames = fileUtil.saveFiles(files);

        // 수정후에도 남아있는 파일 목록을 뽑고
        List<String> uploadedFileNames = productDto.getUploadFileNames();
        // 현재 새롭게 등록한 파일을 기존 목록에 추가해준다.
        if (currentUploadFileNames != null && !currentUploadFileNames.isEmpty()) {
            uploadedFileNames.addAll(currentUploadFileNames);
        }

        productService.modify(productDto);

        // 변경된 후 실제 상품정보에서 빠진 사진은 삭제해야 함
        List<String> oldFileNames = oldProductDto.getUploadFileNames();
        if (oldFileNames != null && !oldFileNames.isEmpty()) {
            // 새로 정리된 파일에 속하지 않은 파일을 찾는다.
//            List<String> removeFiles = oldFileNames.stream().filter(fileName -> uploadedFileNames.indexOf(fileName) == -1).collect(Collectors.toList());
//            List<String> removeFiles = oldFileNames.stream().filter(fileName -> uploadedFileNames.indexOf(fileName) == -1).toList();
            List<String> removeFiles = oldFileNames.stream().filter(fileName -> !uploadedFileNames.contains(fileName)).toList();

            fileUtil.deleteFiles(removeFiles);
        }

        return Map.of("result", "SUCCESS");
    }

    @DeleteMapping("/{pno}") // 실제로는 소프트 딜리트를 하지만 강의는 연습삼아 삭제
    public Map<String, String> remove(@PathVariable("pno") Long pno) {
        List<String> oldFileNames = productService.get(pno).getUploadFileNames();

        productService.remove(pno);

        fileUtil.deleteFiles(oldFileNames);

        return Map.of("result", "SUCCESS");
    }
}
