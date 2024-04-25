package org.hugo.apiserver.repository.search;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPQLQuery;
import lombok.extern.log4j.Log4j2;
import org.hugo.apiserver.domain.Product;
import org.hugo.apiserver.domain.ProductImage;
import org.hugo.apiserver.domain.QProduct;
import org.hugo.apiserver.domain.QProductImage;
import org.hugo.apiserver.dto.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;
import java.util.Objects;

@Log4j2
public class ProductSearchImpl extends QuerydslRepositorySupport implements ProductSearch {

    public ProductSearchImpl() {
        super(Product.class);
    }

    @Override
    public PageResponseDto<ProductDto> searchList(PageRequestDto pageRequestDto) {
        log.info("--------------------------searchList----------------------");

        Pageable pageable = PageRequest.of(
                pageRequestDto.getPage() - 1,
                pageRequestDto.getSize(),
                Sort.by("pno").descending()
        );

        QProduct product = QProduct.product;
        QProductImage productImage = QProductImage.productImage;

        JPQLQuery<Product> query = from(product);
        /**
         * productImage가 엔티티가 아니기 떄문에 이렇게 써야한다.
         * product에 imageList를 productImage로 간주할꺼야~ 라는 의미
         * ElementCollection으로 QueryDsl 쓸 때 이렇게 써야한다.
         */
        query.leftJoin(product.imageList, productImage);

        query.where(productImage.ord.eq(0));

        Objects.requireNonNull(getQuerydsl()).applyPagination(pageable, query);

        List<Tuple> productList = query.select(product, productImage).fetch();

        long count = query.fetchCount();

        log.info("=============================================");
        log.info(productList);
        log.info(count);

        return null;
    }

    @Override // 혼자해본 프로젝션...
    public PageResponseDto<ProjectionProductImageDto> searchListProjection(PageRequestDto pageRequestDto) {
        log.info("--------------------------searchListProjection----------------------");

        Pageable pageable = PageRequest.of(
                pageRequestDto.getPage() - 1,
                pageRequestDto.getSize(),
                Sort.by("pno").descending()
        );

        QProduct product = QProduct.product;
        QProductImage productImage = QProductImage.productImage;

        JPQLQuery<Product> query = from(product);

        query.leftJoin(product.imageList, productImage);

        query.where(productImage.ord.eq(0));

        Objects.requireNonNull(getQuerydsl()).applyPagination(pageable, query);

        List<ProjectionProductImageDto> productList = query.select(
                Projections.constructor(ProjectionProductImageDto.class,
                        product.pno,
                        product.pname,
                        product.price,
                        product.pdesc,
                        product.delFlag,
                        productImage
                )
        ).fetch();

        long count = query.fetchCount();

        log.info("=============================================");
        log.info(productList);
        log.info(count);
        return null;
    }
}
