package org.hugo.apiserver.repository.search;

import org.hugo.apiserver.domain.Todo;
import org.hugo.apiserver.dto.PageRequestDto;
import org.springframework.data.domain.Page;

public interface TodoSearch {

    Page<Todo> search1(PageRequestDto pageRequestDto);
}
