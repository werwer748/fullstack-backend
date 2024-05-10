package org.hugo.apiserver.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hugo.apiserver.domain.Cart;
import org.hugo.apiserver.domain.CartItem;
import org.hugo.apiserver.domain.Member;
import org.hugo.apiserver.domain.Product;
import org.hugo.apiserver.dto.CartItemDto;
import org.hugo.apiserver.dto.CartItemListDto;
import org.hugo.apiserver.repository.CartItemRepository;
import org.hugo.apiserver.repository.CartRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;

    private final CartItemRepository cartItemRepository;

    @Override
    public List<CartItemListDto> addOrModify(CartItemDto cartItemDto) {
        String email = cartItemDto.getEmail();
        Long pno = cartItemDto.getPno();
        int qty = cartItemDto.getQty();
        Long cino  = cartItemDto.getCino();

        if (cino != null) {
            Optional<CartItem> cartItemResult = cartItemRepository.findById(cino);

            CartItem cartItem = cartItemResult.orElseThrow();

            cartItem.changeQty(qty);

            cartItemRepository.save(cartItem);

            return getCartItems(email);
        }

        Cart cart = getCart(email);

        CartItem cartItem = null;

        cartItem = cartItemRepository.getItemOfPno(email, pno);

        if (cartItem == null) {
            Product product = Product.builder().pno(pno).build();
            cartItem = CartItem.builder().product(product).cart(cart).qty(qty).build();
        } else {
            cartItem.changeQty(qty);
        }

        cartItemRepository.save(cartItem);

        return getCartItems(email);
    }

    private Cart getCart(String email) {
        // 해당 이메일의 장바구니가 있는지 확인 있으면 반환

        // 없으면 Cart 객체 생성하고 추가 반환

        Cart cart = null;

        Optional<Cart> result = cartRepository.getCartOfMember(email);

        if (result.isEmpty()) {
            log.info("Cart of the member is not exist!!");
            Member member = Member.builder().email(email).build();
            Cart tempCart = Cart.builder().owner(member).build();

            cart = cartRepository.save(tempCart);
        } else {
            cart = result.get();
        }

        return cart;
    }

    @Override
    public List<CartItemListDto> getCartItems(String email) {
        return cartItemRepository.getItemsOfCartDtoByEmail(email);
    }

    @Override
    public List<CartItemListDto> remove(Long cino) {
        Long cno = cartItemRepository.getCartFromItem(cino);

        cartItemRepository.deleteById(cino);

        return cartItemRepository.getItemsOfCartDtoByCart(cno);
    }
}
