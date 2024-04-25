package org.hugo.apiserver.service;

import org.hugo.apiserver.dto.PageRequestDto;
import org.hugo.apiserver.dto.PageResponseDto;
import org.hugo.apiserver.dto.ProductDto;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface ProductService {
    PageResponseDto<ProductDto> getList(PageRequestDto pageRequestDto);

    Long register(ProductDto productDto);

    ProductDto get(Long pno);

    void modify();
}
