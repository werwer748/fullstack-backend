package org.hugo.apiserver.repository;

import lombok.extern.log4j.Log4j2;
import org.hugo.apiserver.domain.Cart;
import org.hugo.apiserver.domain.CartItem;
import org.hugo.apiserver.domain.Member;
import org.hugo.apiserver.domain.Product;
import org.hugo.apiserver.dto.CartItemListDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
class CartRepositoryTest {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Transactional
    @Commit
    @Test
    public void testInsertByProduct() {
        String email = "user1@aaa.com";
        Long pno = 6L;
        int qty = 4;

        // 사용자 이메일과 상품 번호로 장바구니 아이템 확인 없으면 추가 있으면 수량 변경하여 저장
        CartItem cartItem = cartItemRepository.getItemOfPno(email, pno);

        // 이미 사용자의 장바구니에 상품이 담겨있을 때
        if (cartItem != null) {
            cartItem.changeQty(qty);
            cartItemRepository.save(cartItem);
            return;
        }
        // 사용자의 장바구니에 장바구니-아이템을 만들어서 넣어줘야 한다.
        Optional<Cart> result = cartRepository.getCartOfMember(email);

        Cart cart = null;

        //? builder로 만드는것들은 테이블에 로우형태 맞춰주는 걸로 보임(DB에서 값을 조회해와서 꽂아 넣는게 아니다.)

        // 장바구니가 없다면...? => 장바구니 만들어주기
        if (result.isEmpty()) {
            Member member = Member.builder().email(email).build();
            Cart tempCart = Cart.builder().owner(member).build();

            cart = cartRepository.save(tempCart);
        } else { // 장바구니는 있으나 장바구니-아이템에 해당 상품이 없는 경우
            cart = result.get();
        }

        Product product = Product.builder().pno(pno).build();
        cartItem = CartItem.builder()
                .cart(cart)
                .product(product)
                .qty(qty)
                .build();

        cartItemRepository.save(cartItem);
    }

    @Test
    public void testListOfMember() {
        String email = "user1@aaa.com";

        List<CartItemListDto> cartItemListDtoList = cartItemRepository.getItemsOfCartDtoByEmail(email);

        for (CartItemListDto dto : cartItemListDtoList) {
            log.info(dto);
        }
    }

    @Transactional
    @Commit
    @Test
    public void testUpdateByCino() {

        Long cino = 1L;
        int qty = 4;

        Optional<CartItem> result = cartItemRepository.findById(cino);

        CartItem cartItem = result.orElseThrow();

        cartItem.changeQty(qty);
        cartItemRepository.save(cartItem);
    }

    @Test
    public void testDeleteThenList() {
        Long cino = 1L;

        Long cno = cartItemRepository.getCartFromItem(cino);

        cartItemRepository.deleteById(cino);

        List<CartItemListDto> cartItemList = cartItemRepository.getItemsOfCartDtoByCart(cno);

        for (CartItemListDto dto : cartItemList) {
            log.info(dto);
        }
    }
}