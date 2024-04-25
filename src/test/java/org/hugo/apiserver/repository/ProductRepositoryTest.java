package org.hugo.apiserver.repository;

import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.assertj.core.api.Assertions;
import org.hugo.apiserver.domain.Product;
import org.hugo.apiserver.dto.PageRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Commit;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void testInsert() {
        for (int i = 0; i < 10; i++) {
            Product product = Product.builder()
                    .pname("test Product")
                    .pdesc("test product desc")
                    .price(1000)
                    .build();

            product.addImageString(UUID.randomUUID() + "_" + "IMAGE1.jpg");
            product.addImageString(UUID.randomUUID() + "_" + "IMAGE2.jpg");

            productRepository.save(product);
        }
    }

    // 에러 테스트
//    @Transactional // 주석치니 에러 - no Session
    @Test // 트랜잭션을 걸면 상품조회, 상품이미지 조회로 쿼리가 두번 나간다. - 기본적으로 Lazy로딩이기 떄문에..(죠인해줘야하는데 이걸 자동으로 하게끔 만들예정-레포지토리에)
    public void testRead() {
        Long pno = 1L;

//        Optional<Product> result = productRepository.findById(pno); // 이전 트랜잭션 에러코드 테스트 (select 두번)
        Optional<Product> result = productRepository.selectOne(pno);

        Product product = result.orElseThrow();

        log.info(product);
        log.info(product.getImageList());
    }

    @Test
    @Commit
    @Transactional
    public void testDelete() {
        Long pno = 2L;

        productRepository.updateToDelete(pno, true);
    }

    @Test // 혼자 테스트해본거.. 이런식으로 정적쿼리도 쓸 수 있는듯 - 자주 활용하자!
    public void testAllProductDelFlagIsFalse() {
        List<Product> allByDelFlagIsFalse = productRepository.findAllByDelFlagIsFalse();

        log.info(allByDelFlagIsFalse);
        assertThat(allByDelFlagIsFalse.size()).isEqualTo(1);
    }

    @Test
    public void testUpdate() {
        Product product = productRepository.selectOne(1L).get();
        product.changePrice(3000);

        // 리스트를 비우고 다시 채우는 이유? jpa 연관관계 컬렉션등이 포함된 경우는 그 컬렉션을 계속 쓰는것이 좋다. 다른 ArrayList가되면 문제가 심각해짐.
        // 이전 이미지 2개가 db에서도 날아가는거 확인함.
        product.clearList();

        // 이렇게 한번에 처리되는 경우 엘리멘트컬렉션을 쓰는게 편하다.
        // 그니까 결국 일대다에서 주인을 일로 쓰고싶은경우는 이러는게 관리도 편하고 좋다..? 특히 일 쪽에서 다를 모두 컨트롤하는 상황이라면 더더욱...?
        product.addImageString(UUID.randomUUID() + "_" + "PIMAGE1.jpg");
        product.addImageString(UUID.randomUUID() + "_" + "PIMAGE2.jpg");
        product.addImageString(UUID.randomUUID() + "_" + "PIMAGE3.jpg");

        productRepository.save(product);
    }

    @Test
    public void testList() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("pno").descending());

        Page<Object[]> result = productRepository.selectList(pageable);

        // join 된 쿼리와 카운트쿼리만 딱 나감.
        result.getContent().forEach(arr -> log.info(Arrays.toString(arr)));
    }

    @Test
    public void testSearch() {
        PageRequestDto pageRequestDto = PageRequestDto.builder().build();

        productRepository.searchList(pageRequestDto);
    }

    @Test
    public void testProjectionSearch() {
        PageRequestDto pageRequestDto = PageRequestDto.builder().build();

        productRepository.searchListProjection(pageRequestDto);
    }
}