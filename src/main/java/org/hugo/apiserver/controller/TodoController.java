package org.hugo.apiserver.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hugo.apiserver.dto.PageRequestDto;
import org.hugo.apiserver.dto.PageResponseDto;
import org.hugo.apiserver.dto.TodoDto;
import org.hugo.apiserver.service.TodoService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

//@CrossOrigin // 컨트롤러에 CORS 설정하는 방법
@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/todo")
public class TodoController {
    private final TodoService todoService;

    @GetMapping("/{tno}")//PathVariable로 불러오는 데이터는 누구나 똑같은 데이터를 볼 수 있음
    public TodoDto get(@PathVariable("tno") Long tno) {

        // 서비스에서 Dto로 값을 반환하고 그걸 내보내기때문에 이런식으로 써도 좋지만 예외상황이 발생했을 때 문제가된다.
        return todoService.get(tno);
    }

    @GetMapping("/list") // queryString같은 경우는 부를 때마다 데이터가 변경될 수 있는 경우 - 이런 구분은 강사 기준이라고 함
    public PageResponseDto<TodoDto> list(PageRequestDto pageRequestDto) {
        log.info("list.............." + pageRequestDto);
        return todoService.getList(pageRequestDto);
    }

    @PostMapping
    public Map<String, Long> register(@RequestBody TodoDto dto) {
        log.info("todoDto: " + dto);

        Long tno = todoService.register(dto);

        return Map.of("TNO", tno);
    }

    @PutMapping("/{tno}")
    public Map<String, String> modify(
            @PathVariable("tno") Long tno,
            @RequestBody TodoDto dto
    ) {
        dto.setTno(tno);
        todoService.modify(dto);
        return Map.of("RESULT", "SUCCESS");
    }

    @DeleteMapping("/{tno}")
    public Map<String, String> remove(@PathVariable("tno") Long tno) {
        todoService.remove(tno);
        return Map.of("RESULT", "SUCCESS");
    }
}
