package org.hugo.apiserver.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hugo.apiserver.domain.Product;
import org.hugo.apiserver.domain.ProductImage;
import org.hugo.apiserver.dto.PageRequestDto;
import org.hugo.apiserver.dto.PageResponseDto;
import org.hugo.apiserver.dto.ProductDto;
import org.hugo.apiserver.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    @Override
    public void remove(Long pno) {
        productRepository.deleteById(pno);
    }

    private final ProductRepository productRepository;

    @Override
    public PageResponseDto<ProductDto> getList(PageRequestDto pageRequestDto) {
        Pageable pageable = PageRequest.of(
                pageRequestDto.getPage() - 1,
                pageRequestDto.getSize(),
                Sort.by("pno").descending()
        );

        // Object[] => {0 product 1 productImage}[]
        Page<Object[]> result = productRepository.selectList(pageable);

        // get을 쓰면 스트림으로 나오기때문에 바로 맵을 뽑아올 수 있다.
        List<ProductDto> dtoList = result.get().map(prd -> {
            ProductDto productDto = null;

            Product product = (Product) prd[0];
            ProductImage productImage = (ProductImage) prd[1];

            productDto = ProductDto.builder()
                    .pno(product.getPno())
                    .pname(product.getPname())
                    .pdesc(product.getPdesc())
                    .price(product.getPrice())
                    .build();

            String imageStr = productImage.getFileName();
            productDto.setUploadFileNames(List.of(imageStr));

            return productDto;
        }).collect(Collectors.toList());

        long totalCount = result.getTotalElements();

        return PageResponseDto.<ProductDto>withAll()
                .dtoList(dtoList)
                .total(totalCount)
                .pageRequestDto(pageRequestDto)
                .build();
    }

    @Override
    public Long register(ProductDto productDto) {
        Product product = dtoToEntity(productDto);

        log.info("-------------------------------");
        log.info(product);
        log.info(product.getImageList());

        return productRepository.save(product).getPno();
    }

    @Override
    public ProductDto get(Long pno) {
        Optional<Product> result = productRepository.findById(pno);

        Product product = result.orElseThrow();

        return entityToDto(product);
    }

    @Override
    public void modify(ProductDto productDto) {
        // 조회하고
        Optional<Product> result = productRepository.findById(productDto.getPno());

        Product product = result.orElseThrow();
        // 변경 내용 반영하고
        product.changePrice(productDto.getPrice());
        product.changeName(productDto.getPname());
        product.changeDesc(productDto.getPdesc());
        product.changeDel(product.isDelFlag());

        // 이미지 처리
        List<String> uploadFileNames = productDto.getUploadFileNames();

        product.clearList();

        if (uploadFileNames != null && !uploadFileNames.isEmpty()) {
//            uploadFileNames.forEach(uploadName -> {
//                product.addImageString(uploadName);
//            });
            uploadFileNames.forEach(product::addImageString);
        }

        // 저장
        productRepository.save(product);
    }

    private ProductDto entityToDto(Product product) {
        ProductDto productDto = ProductDto.builder()
                .pno(product.getPno())
                .pname(product.getPname())
                .pdesc(product.getPdesc())
                .price(product.getPrice())
                .delFlag(product.isDelFlag())
                .build();

        List<ProductImage> imageList = product.getImageList();

        if (imageList == null || imageList.isEmpty()) {
            return productDto;
        }

//        List<String> fileNameList = imageList.stream().map(productImage -> productImage.getFileName()).toList();
        List<String> fileNameList = imageList.stream().map(ProductImage::getFileName).toList();

        productDto.setUploadFileNames(fileNameList);

        return productDto;
    }

    private Product dtoToEntity(ProductDto productDto) {
        Product product = Product.builder()
                .pno(productDto.getPno())
                .pname(productDto.getPname())
                .pdesc(productDto.getPdesc())
                .price(productDto.getPrice())
                .build();

        List<String> uploadFileNames = productDto.getUploadFileNames();

        if (uploadFileNames == null || uploadFileNames.isEmpty()) {
            return product;
        }

//        uploadFileNames.forEach(filename -> {
//            product.addImageString(filename);
//        });
        uploadFileNames.forEach(product::addImageString);

        return product;
    }
}
