package org.hugo.apiserver.repository;

import org.hugo.apiserver.domain.Product;
import org.hugo.apiserver.repository.search.ProductSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductSearch {
    @EntityGraph(attributePaths = "imageList") // 엔티티그래프를 통해서 함꼐 조회하도록(죠인해서 한번에 불러오도록 함)
    @Query("select p from Product p where p.pno = :pno")
    Optional<Product> selectOne(@Param("pno") Long pno);

    @Modifying // 업데이트 딜리트등에서 사용하면 됨.
    @Query("update Product p set p.delFlag = :delFlag where p.pno = :pno")
    void updateToDelete(@Param("pno") Long pno, @Param("delFlag") boolean delFlag);

    // 혼자 테스트해본거.. 이런식으로 정적쿼리도 쓸 수 있는듯 - 자주 활용하자!
    List<Product> findAllByDelFlagIsFalse();

    // 죠인에 테이블이 아닌 죠인되는 필드값을 씀.
    @Query("select p, pi from Product p left join p.imageList pi where pi.ord = 0 and p.delFlag = false")
    Page<Object[]> selectList(Pageable pageable);
}
