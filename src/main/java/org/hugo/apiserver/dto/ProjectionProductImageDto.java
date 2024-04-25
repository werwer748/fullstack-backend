package org.hugo.apiserver.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hugo.apiserver.domain.ProductImage;

import java.util.List;

@Data
public class ProjectionProductImageDto {
    private Long pno;
    private String pname;
    private int price;
    private String pdesc;
    private boolean delFlag;
    private ProductImage productImage;

//    @QueryProjection
    public ProjectionProductImageDto(Long pno, String pname, int price, String pdesc, boolean delFlag, ProductImage productImage) {
        this.pno = pno;
        this.pname = pname;
        this.price = price;
        this.pdesc = pdesc;
        this.delFlag = delFlag;
        this.productImage = productImage;
    }
}
