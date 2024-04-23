package org.hugo.apiserver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

// dto는 자유로운 영혼... 막 쓰고 버리는 존재, 상황에 따라 많이만들어 써도 된다.
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor // json으로 변환하려면 비어있는 생성자 필요함.
public class TodoDto {

    private Long tno;

    private String title;

    private String content;

    private boolean complete;

    private LocalDate dueDate;
}
