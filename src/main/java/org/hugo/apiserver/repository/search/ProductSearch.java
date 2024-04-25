package org.hugo.apiserver.repository.search;

import org.hugo.apiserver.dto.PageRequestDto;
import org.hugo.apiserver.dto.PageResponseDto;
import org.hugo.apiserver.dto.ProductDto;
import org.hugo.apiserver.dto.ProjectionProductImageDto;

public interface ProductSearch {
    PageResponseDto<ProductDto> searchList(PageRequestDto pageRequestDto);
    PageResponseDto<ProjectionProductImageDto> searchListProjection(PageRequestDto pageRequestDto);
}
