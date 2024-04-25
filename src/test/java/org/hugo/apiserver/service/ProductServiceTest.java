package org.hugo.apiserver.service;

import lombok.extern.log4j.Log4j2;
import org.hugo.apiserver.dto.PageRequestDto;
import org.hugo.apiserver.dto.PageResponseDto;
import org.hugo.apiserver.dto.ProductDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
class ProductServiceTest {
    @Autowired
    private ProductService productService;

    @Test
    public void testList() {
        PageRequestDto pageRequestDto = PageRequestDto.builder().build();

        PageResponseDto<ProductDto> responseDto = productService.getList(pageRequestDto);

        log.info(responseDto.getDtoList());
    }

    @Test
    public void testRegister() {
        ProductDto productDto = ProductDto.builder()
                .pname("새로운 상품")
                .pdesc("테스트코드로 등록한 새로운 상품")
                .price(10000)
                .build();

        productDto.setUploadFileNames(
                List.of(
                        UUID.randomUUID() + "_" + "TestImage1.jpeg",
                        UUID.randomUUID() + "_" + "TestImage2.jpeg"
                )
        );

        productService.register(productDto);
    }
}