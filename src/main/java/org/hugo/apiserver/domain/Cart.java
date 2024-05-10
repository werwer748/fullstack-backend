package org.hugo.apiserver.domain;

import jakarta.persistence.*;
import lombok.*;

/**
 * 추후에 서비스가 커지고 DB가 나눠지고 하다보면
 * 유저 중심의 테이블관계 형성보다는 이런식으로 계층을 나누는 것도 좋다고 함.
 */
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(exclude = "owner")
@Table(
        name = "tbl_cart",
        indexes = { @Index(name = "idx_cart_email", columnList = "member_owner") }
)
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cno;

    @OneToOne
    @JoinColumn(name = "member_owner")
    private Member owner;
}