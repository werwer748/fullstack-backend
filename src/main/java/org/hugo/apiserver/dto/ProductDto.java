package org.hugo.apiserver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {

    private Long pno;

    private String pname;

    private int price;

    private String pdesc;

    private boolean delFlag;

    // 한 엔티티에 여러가지 요청에 사용가능한 필드값을 정의하여 사용하는 것도 좋은 방법인듯?
    @Builder.Default // 업로드시 사용 - 진짜 사진 파일
    private List<MultipartFile> files = new ArrayList<>();

    @Builder.Default // 데이터 내려줄때 DB에서 꺼내온 값
    private List<String> uploadFileNames = new ArrayList<>();
}
