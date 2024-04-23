package org.hugo.apiserver.service;

import lombok.extern.log4j.Log4j2;
import org.hugo.apiserver.dto.PageRequestDto;
import org.hugo.apiserver.dto.TodoDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
class TodoServiceTest {

    @Autowired
    TodoService todoService;

    @Test
    public void testGet() {
        Long tno = 50L;

        log.info(todoService.get(tno));
    }

    @Test
    public void testRegister() {
        TodoDto todoDto = TodoDto.builder()
                .title("test title")
                .content("test content")
                .dueDate(LocalDate.of(2024, 5, 10))
                .build();

        log.info(todoService.register(todoDto));
    }

    @Test
    public void testGetList() {
        PageRequestDto pageRequestDto = PageRequestDto.builder().page(11).build();
        log.info(todoService.getList(pageRequestDto));
    }
}