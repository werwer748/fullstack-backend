package org.hugo.apiserver.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "tbl_product")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "imageList") // 연관관계나 엘리먼트콜렉션이 적용될때 신경써줘야한다. exclude로 빼줘야 함.
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pno;

    private String pname;

    private int price;

    private String pdesc;

    private boolean delFlag;

    /**
     * @ElementCollection 은 엔티티의 주인은 아니다.
     * 상품을 관리하는 과정에 이미지를 처리하는 것도 포함되어 있다는걸 생각해보면 됨.
     * 그래서 엔티티에서 ElementCollection을 처리해주는게 엔티티내에 있는게 좋다.
     * 굳이 테이블을 나눠서 관계를 주는 것 보다 상품이미지를 상품에서 관리한다~ 정도의 느낌으로 이렇게 쓰는것도 좋은 방법임
     * 라이프 사이클이 이 엔티티에 맞춰진다.
     */
    @ElementCollection
    @Builder.Default
    private List<ProductImage> imageList = new ArrayList<>();

    public void changePrice(int price) { // setter를 활용해도 되지만 강의를 따라가기 편한방법으로
        this.price = price;
    }

    public void changeDesc(String desc) {
        this.pdesc = desc;
    }

    public void changeName(String name) {
        this.pname = name;
    }

    public void changeDel(boolean delFlag) {
        this.delFlag = delFlag;
    }

    //=== 이미지 관리를 위한 메서드 ===//
    public void addImage(ProductImage image) { // 별도의 트랜잭션등을 신경쓰지 않아도 된다는 장점이 있다.
        image.setOrd(imageList.size());
        imageList.add(image);
    }
    // imageList가 새로운 객체가 되면 안되기때문에 메서드를 활용해 imageList에 쌓아주는 방식으로 쓰기 위해 만든것
    public void addImageString(String fileName) {
        ProductImage productImage = ProductImage.builder()
                .fileName(fileName)
                .build();

        addImage(productImage);
    }

    public void clearList() {
        this.imageList.clear();
    }
}
