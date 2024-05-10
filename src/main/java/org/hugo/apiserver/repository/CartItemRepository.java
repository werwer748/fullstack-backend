package org.hugo.apiserver.repository;

import org.hugo.apiserver.domain.CartItem;
import org.hugo.apiserver.dto.CartItemListDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // 특정한 사용자의 모든 장바구니 아이템들을 가져올 경우 => 이메일을 받아서 CartItemListDto로 반환
    //? Tip1. 긴 쿼리를 사용할 경우 테스트를 작성해 쿼리를 확인해가면서 살을 붙여나가면 편하다.
    //? Tip2. 경로를 사용해 dto로 프로젝션
    @Query("select " +
            "new org.hugo.apiserver.dto.CartItemListDto(" +
            "ci.cino, ci.qty, p.pno, p.pname, p.price, pi.fileName " +
            ") " +
            "from CartItem ci " +
            "inner join Cart mc on ci.cart = mc " +
            "left join Product p on ci.product = p " +
            "left join p.imageList pi " + // ElementCollection은 이런식으로 on이 필요 없음
            "where " +
            "mc.owner.email = :email " +
            "and pi.ord = 0 " +
            "order by ci.cino desc")
    List<CartItemListDto> getItemsOfCartDtoByEmail(@Param("email") String email);

    // 이메일(사용자), 상품번호로 해당 상품이 이미 장바구니 아이템으로 존재하는지 확인
    @Query("select ci from CartItem ci " +
            "left join Cart c on ci.cart = c " +
            "where c.owner.email = :email and ci.product.pno = :pno")
    CartItem getItemOfPno(@Param("email") String email, @Param("pno") Long pno);

    // 장바구니 아이템 번호로 장바구니를 얻어오려고 하는 경우
    @Query("select c.cno from Cart c left join CartItem ci on ci.cart = c where ci.cino = :cino")
    Long getCartFromItem(@Param("cino") Long cino);

    // 장바구니 번호로 모든 장바구니 아이템을 조회
    @Query("select " +
            "new org.hugo.apiserver.dto.CartItemListDto(" +
            "ci.cino, ci.qty, p.pno, p.pname, p.price, pi.fileName " +
            ") " +
            "from CartItem ci " +
            "inner join Cart mc on ci.cart = mc " +
            "left join Product p on ci.product = p " +
            "left join p.imageList pi " +
            "where pi.ord = 0 " +
            "and mc.cno = :cno " +
            "order by ci.cino desc ")
    List<CartItemListDto> getItemsOfCartDtoByCart(@Param("cno") Long cno);
}
