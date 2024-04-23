package org.hugo.apiserver.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hugo.apiserver.domain.Todo;
import org.hugo.apiserver.dto.PageRequestDto;
import org.hugo.apiserver.dto.PageResponseDto;
import org.hugo.apiserver.dto.TodoDto;
import org.hugo.apiserver.repository.TodoRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepository;

    @Override
    public TodoDto get(Long tno) {

        Optional<Todo> result = todoRepository.findById(tno);

        Todo todo = result.orElseThrow();

        return entityToDto(todo);
    }

    @Override
    public Long register(TodoDto dto) {
        Todo todo = dtoToEntity(dto); // pk가 없음

        Todo result = todoRepository.save(todo); // 저장됐기 때문에 pk가 있음

        return result.getTno();
    }

    @Override
    public void modify(TodoDto dto) {
        Optional<Todo> result = todoRepository.findById(dto.getTno());

        Todo todo = result.orElseThrow();

        todo.changeTitle(dto.getTitle());
        todo.changeContent(dto.getContent());
        todo.changeComplete(dto.isComplete());
        todo.changeDueDate(dto.getDueDate());

        todoRepository.save(todo);
    }

    @Override
    public void remove(Long tno) {
        todoRepository.deleteById(tno);
    }

    @Override
    public PageResponseDto<TodoDto> getList(PageRequestDto pageRequestDto) {
        // JPA
        Page<Todo> result = todoRepository.search1(pageRequestDto);

        // Todo엔티티에서 가져온 결과를 TodoDto로 변환해야함
        List<TodoDto> dtoList = result.get().map(this::entityToDto).toList();

        PageResponseDto<TodoDto> responseDto =
                PageResponseDto.<TodoDto>withAll()
                        .dtoList(dtoList)
                        .pageRequestDto(pageRequestDto)
                        .total(result.getTotalElements())
                        .build();

        return responseDto;
    }
}
