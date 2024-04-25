package org.hugo.apiserver.domain;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductImage {

    private String fileName;

    @Setter
    private int ord; // 순번? - 대표이미지 설정을 위해서
}
