package org.hugo.apiserver.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hugo.apiserver.dto.CartItemDto;
import org.hugo.apiserver.dto.CartItemListDto;
import org.hugo.apiserver.service.CartService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    // 시큐리티가 확인한 이메일과 요청에서의 이메일이 일치하는지 확인
    @PreAuthorize("#itemDto.email == authentication.name") // 다른이메일로 접근시 ERROR_ACCESSDENIED
    @PostMapping("/change")
    public List<CartItemListDto> changeCart(@RequestBody CartItemDto itemDto) {
        log.info(itemDto);

        if (itemDto.getQty() <= 0) {
            return cartService.remove(itemDto.getCino());
        }

        return cartService.addOrModify(itemDto);
    }

    @PreAuthorize("hasRole('ROLE_USER')") // hasAnyRole 등을 써도 된다.
    @GetMapping("/items")
    //? Principal: 시큐리티를 통해 현재 로그인한 사용자 정보를 가져올 수 있다.
    public List<CartItemListDto> getCartItems(Principal principal) {
        String email = principal.getName();
        log.info("email: " + email);

        return cartService.getCartItems(email);
    }

    @PreAuthorize("hasRole('ROLE_USER')") // hasAnyRole 등을 써도 된다.
    @GetMapping("/{cino}")
    public List<CartItemListDto> removeFromCart(@PathVariable("cino") Long cino) {

        log.info("cart item no: " + cino);

        return cartService.remove(cino);
    }
}
