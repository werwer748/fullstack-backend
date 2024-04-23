package org.hugo.apiserver.service;

import jakarta.transaction.Transactional;
import org.hugo.apiserver.domain.Todo;
import org.hugo.apiserver.dto.PageRequestDto;
import org.hugo.apiserver.dto.PageResponseDto;
import org.hugo.apiserver.dto.TodoDto;

@Transactional
//? interface로 설계한 이유? AOP 같은거 적용할 때 편함.
public interface TodoService {

    TodoDto get(Long tno);

    Long register(TodoDto dto);

    void modify(TodoDto dto);

    void remove(Long tno);

    PageResponseDto<TodoDto> getList(PageRequestDto pageRequestDto);

    // interface는 실질적인 구현이 안되지만 default를 활용하면 기능이나 메서드같은것을 선언해 줄 수 있다.
    default TodoDto entityToDto(Todo todo) {
        return  TodoDto.builder()
                        .tno(todo.getTno())
                        .title(todo.getTitle())
                        .content(todo.getContent())
                        .complete(todo.isComplete())
                        .dueDate(todo.getDueDate())
                        .build();
    }

    default Todo dtoToEntity(TodoDto todoDto) {
        return  Todo.builder()
                .tno(todoDto.getTno())
                .title(todoDto.getTitle())
                .content(todoDto.getContent())
                .complete(todoDto.isComplete())
                .dueDate(todoDto.getDueDate())
                .build();
    }

}
