package org.hugo.apiserver.service;

import org.hugo.apiserver.dto.CartItemDto;
import org.hugo.apiserver.dto.CartItemListDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface CartService {

    List<CartItemListDto> addOrModify(CartItemDto cartItemDto);

    List<CartItemListDto> getCartItems(String email);

    List<CartItemListDto> remove(Long cino);
}
