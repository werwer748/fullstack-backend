package org.hugo.apiserver.repository.search;

import com.querydsl.jpa.JPQLQuery;
import lombok.extern.log4j.Log4j2;
import org.hugo.apiserver.domain.QTodo;
import org.hugo.apiserver.domain.Todo;
import org.hugo.apiserver.dto.PageRequestDto;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

@Log4j2
public class TodoSearchImpl extends QuerydslRepositorySupport implements TodoSearch {

    public TodoSearchImpl() {
        super(Todo.class);
    }

    @Override
    public Page<Todo> search1(PageRequestDto pageRequestDto) {
        log.info("search1 start..............");
        QTodo todo = QTodo.todo;

        JPQLQuery<Todo> query = from(todo);

        Pageable pageable = PageRequest.of(
                pageRequestDto.getPage() - 1,
                pageRequestDto.getSize(),
                Sort.by("tno").descending()
        );

        this.getQuerydsl().applyPagination(pageable, query); // 이렇게 페이징 데이터를 가져올 수 있다.

        List<Todo> list = query.fetch();// 목록 데이터
        long total = query.fetchCount();// 디프리케이트 안됐네...?

        return new PageImpl<>(list, pageable, total);
    }
}
