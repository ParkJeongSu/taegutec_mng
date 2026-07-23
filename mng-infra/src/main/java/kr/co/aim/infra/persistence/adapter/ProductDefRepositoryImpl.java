package kr.co.aim.infra.persistence.adapter;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.common.condition.ProductDefSearchCondition;
import kr.co.aim.domain.model.ProductDef;
import kr.co.aim.domain.repository.ProductDefRepository;
import kr.co.aim.infra.persistence.entity.ProductDefEntity;
import kr.co.aim.infra.persistence.mapper.ProductDefHistoryMapper;
import kr.co.aim.infra.persistence.mapper.ProductDefMapper;
import kr.co.aim.infra.persistence.springdatajpa.ProductDefHistoryJpaRepository;
import kr.co.aim.infra.persistence.springdatajpa.ProductDefJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static kr.co.aim.infra.persistence.entity.QProductDefEntity.productDefEntity;

/**
 * UserRepository의 JPA 기반 구현체.
 * 실제 DB 작업은 Spring Data JPA가 제공하는 JpaRepository에 위임합니다.
 */

@Repository
@RequiredArgsConstructor
public class ProductDefRepositoryImpl implements ProductDefRepository {
    // Spring Data JPA가 자동으로 구현해주는 JPA 리포지토리. UserEntity를 다룬다.
    private final ProductDefJpaRepository productDefJpaRepository;
    private final ProductDefMapper productDefMapper;
    private final ProductDefHistoryJpaRepository productDefHistoryJpaRepository;
    private final ProductDefHistoryMapper productDefHistoryMapper;
    private final JPAQueryFactory queryFactory; // ✨ JPAQueryFactory 주입

    @Override
    public List<ProductDef> findAll() {
        return productDefJpaRepository.findAll().stream().map(productDefMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<ProductDef> findById(Long id) {
        return productDefJpaRepository.findById(id).map(productDefMapper::toDomain);
    }

    @Override
    public Optional<ProductDef> findByProductDefName(String productDefName) {
        return productDefJpaRepository.findByProductDefName(productDefName).map(productDefMapper::toDomain);
    }

    @Override
    public ProductDef save(ProductDef productDef) {
        // 1. Domain -> Entity 변환
        ProductDefEntity entity = productDefMapper.toEntity(productDef);
        // 2. JPA 리포지토리를 통해 DB에 저장
        ProductDefEntity savedEntity = productDefJpaRepository.save(entity);
        // 3. 저장된 Entity -> Domain 변환 후 반환
        return productDefMapper.toDomain(savedEntity);
    }

    @Override
    public List<ProductDef> save(List<ProductDef> productDefList) {
        List<ProductDef> result = new ArrayList<>();
        for(ProductDef  productDef : productDefList) {

            Optional<ProductDefEntity> optionalProductDefEntity = productDefJpaRepository.findByProductDefName(productDef.getProductDefName());

            if(optionalProductDefEntity.isPresent()) {
                ProductDefEntity entity = optionalProductDefEntity.get();
                ProductDefEntity savedEntity = productDefJpaRepository.save(entity);
                ProductDef savedDomain = productDefMapper.toDomain(savedEntity);
                result.add(savedDomain);
            }else{
                // 1. Domain -> Entity 변환
                ProductDefEntity entity = productDefMapper.toEntity(productDef);
                // 2. JPA 리포지토리를 통해 DB에 저장
                ProductDefEntity savedEntity = productDefJpaRepository.save(entity);
                // 3. 저장된 Entity -> Domain 변환 후 반환
                ProductDef savedDomain = productDefMapper.toDomain(savedEntity);
                result.add(savedDomain);
            }
        }

        return result;
    }

    @Override
    public void deleteAllByIdInBatch(List<Long> ids) {
        productDefJpaRepository.deleteAllByIdInBatch(ids);
    }

    @Override
    public Page<ProductDef> findProductDefWithConditions(ProductDefSearchCondition condition, Pageable pageable) {
        // 1. 공통 쿼리 빌더 생성 (SELECT, FROM, JOIN, WHERE)
        JPAQuery<ProductDefEntity> query = queryFactory
                .selectFrom(productDefEntity)
                .where(
                        productDefNameContains(condition.getProductDefName()),
                        factoryNameContains(condition.getFactoryName()),
                        description1Contains(condition.getDescription1()),
                        description2Contains(condition.getDescription2()),
                        ratioEq(condition.getRatio()),
                        defaultReceiveQuantityEq(condition.getDefaultReceiveQuantity())
                );

        // 2. 정렬 적용
        query.orderBy(getOrderSpecifiers(pageable.getSort()));

        // 3. 페이징 적용 (isPaged()로 분기)
        if (pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        // 4. 데이터 조회
        List<ProductDefEntity> content = query.fetch();

        List<ProductDef> converted = content.stream().map(productDefMapper::toDomain).collect(Collectors.toList());

        // 5. 카운트 조회 (isPaged()로 분기)
        long total;
        if (pageable.isPaged()) {
            // [페이징 O] 별도 카운트 쿼리 실행
            Long count = queryFactory
                    .select(productDefEntity.count())
                    .from(productDefEntity)
                    .where(
                            productDefNameContains(condition.getProductDefName()),
                            factoryNameContains(condition.getFactoryName()),
                            description1Contains(condition.getDescription1()),
                            description2Contains(condition.getDescription2()),
                            ratioEq(condition.getRatio()),
                            defaultReceiveQuantityEq(condition.getDefaultReceiveQuantity())
                    )
                    .fetchOne();

            total = (count != null) ? count.longValue() : 0L;

        } else {
            // [페이징 X] .unpaged() 일 때
            total = content.size();
        }

        // 6. PageImpl 반환
        return new PageImpl<>(converted, pageable, total);
    }

    /**
     * Pageable의 Sort 객체를 Querydsl의 OrderSpecifier 배열로 변환합니다.
     */
    private OrderSpecifier<?>[] getOrderSpecifiers(Sort sort) {
        List<OrderSpecifier> orders = new ArrayList<>();

        if (sort.isSorted()) {
            for (Sort.Order order : sort) {
                // 정렬 방향을 결정합니다 (ASC or DESC)
                Order direction = order.isAscending() ? Order.ASC : Order.DESC;

                // 정렬할 속성(컬럼)을 PathBuilder를 통해 지정합니다.
                // "userName"과 같은 문자열을 Q-Type 경로로 변환해줍니다.
                PathBuilder pathBuilder = new PathBuilder<>(productDefEntity.getType(), productDefEntity.getMetadata());

                orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
            }
        }

        // 기본 정렬 조건 (만약 정렬 조건이 없다면 id 내림차순)
        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(Order.DESC, productDefEntity.id));
        }

        return orders.toArray(new OrderSpecifier[0]);
    }

    // == 동적 쿼리를 위한 BooleanExpression 메소드들 ==

    private BooleanExpression productDefNameContains(String productDefName) {
        return StringUtils.hasText(productDefName) ? productDefEntity.productDefName.contains(productDefName) : null;
    }

    private BooleanExpression factoryNameContains(String factoryName) {
        return StringUtils.hasText(factoryName) ? productDefEntity.factoryName.contains(factoryName) : null;
    }

    private BooleanExpression description1Contains(String description1) {
        return StringUtils.hasText(description1) ? productDefEntity.description1.contains(description1) : null;
    }

    private BooleanExpression description2Contains(String description2) {
        return StringUtils.hasText(description2) ? productDefEntity.description2.contains(description2) : null;
    }

    private BooleanExpression ratioEq(BigDecimal ratio) {
        return ratio != null ? productDefEntity.ratio.eq(ratio) : null;
    }

    private BooleanExpression defaultReceiveQuantityEq(BigDecimal defaultReceiveQuantity) {
        return defaultReceiveQuantity != null ? productDefEntity.defaultReceiveQuantity.eq(defaultReceiveQuantity) : null;
    }
}
