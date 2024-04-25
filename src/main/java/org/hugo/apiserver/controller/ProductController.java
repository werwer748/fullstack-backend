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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

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

        return Map.of("result", pno);
    }

    @GetMapping("/{pno}")
    public ProductDto read(@PathVariable("pno") Long pno) {
        return productService.get(pno);
    }
}
