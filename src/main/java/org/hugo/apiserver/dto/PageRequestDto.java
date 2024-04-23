package org.hugo.apiserver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder // 상속이 필요할때 사용할 수 있다.
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageRequestDto {
    @Builder.Default
    private int page = 1;

    @Builder.Default
    private int size = 10;
}
