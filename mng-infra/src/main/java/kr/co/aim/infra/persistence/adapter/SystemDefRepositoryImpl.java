package kr.co.aim.infra.persistence.adapter;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.common.dto.*;
import kr.co.aim.domain.model.SystemDef;
import kr.co.aim.domain.repository.SystemDefRepository;
import kr.co.aim.infra.persistence.entity.SystemDefEntity;
import kr.co.aim.infra.persistence.mapper.SystemDefMapper;
import kr.co.aim.infra.persistence.springdatajpa.SystemDefJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import kr.co.aim.common.dto.QSystemDefResponseDto;

import static kr.co.aim.infra.persistence.entity.QSystemDefEntity.systemDefEntity;

/**
 * UserRepository의 JPA 기반 구현체.
 * 실제 DB 작업은 Spring Data JPA가 제공하는 JpaRepository에 위임합니다.
 */

@Repository
@RequiredArgsConstructor
public class SystemDefRepositoryImpl implements SystemDefRepository {

    private final SystemDefJpaRepository systemDefJpaRepository;
    private final SystemDefMapper systemDefMapper;
    private final JPAQueryFactory queryFactory; // ✨ JPAQueryFactory 주입

    @Override
    public List<SystemDef> findAll() {
        return systemDefJpaRepository.findAll().stream().map(systemDefMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<SystemDef> findById(Long id) {
        return systemDefJpaRepository.findById(id).map(systemDefMapper::toDomain);
    }

    @Override
    public Optional<SystemDef> findBySystemDefName(String systemDefName) {
        return systemDefJpaRepository.findBySystemDefName(systemDefName).map(systemDefMapper::toDomain);
    }

    @Override
    public SystemDef save(SystemDef systemDef) {
        // 1. Domain -> Entity 변환
        SystemDefEntity entity = systemDefMapper.toEntity(systemDef);
        // 2. JPA 리포지토리를 통해 DB에 저장
        SystemDefEntity savedEntity = systemDefJpaRepository.save(entity);
        // 3. 저장된 Entity -> Domain 변환 후 반환
        return systemDefMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteAllByIdInBatch(List<Long> ids) {
        systemDefJpaRepository.deleteAllByIdInBatch(ids);
    }

    @Override
    public Page<SystemDefResponseDto> findSystemDefWithConditions(SystemDefSearchConditionDto condition, Pageable pageable) {

        // 1. 공통 쿼리 빌더 생성 (SELECT, FROM, WHERE)
        JPAQuery<SystemDefResponseDto> query = queryFactory
                .select(
                        new QSystemDefResponseDto(
                                systemDefEntity.id,
                                systemDefEntity.systemDefName,
                                systemDefEntity.checkOutState,
                                systemDefEntity.checkOutTime,
                                systemDefEntity.checkOutUser,
                                systemDefEntity.dataState,
                                systemDefEntity.eventName,
                                systemDefEntity.eventTime,
                                systemDefEntity.eventUser,
                                systemDefEntity.eventComment
                        )
                )
                .from(systemDefEntity)
                .where(
                        systemDefNameContains(condition.getSystemDefName())
                );

        // 2. 정렬 적용
        query.orderBy(getOrderSpecifiers(pageable.getSort()));

        // 3. 페이징 적용 (isPaged()로 분기)
        if (pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        // 4. 데이터 조회
        List<SystemDefResponseDto> content = query.fetch();

        // 5. 카운트 조회 (isPaged()로 분기)
        long total;
        if (pageable.isPaged()) {
            // [페이징 O] 별도 카운트 쿼리 실행
            Long count = queryFactory
                    .select(systemDefEntity.count())
                    .from(systemDefEntity)
                    .where(
                            systemDefNameContains(condition.getSystemDefName())
                    )
                    .fetchOne();

            total = (count != null) ? count.longValue() : 0L;

        } else {
            // [페이징 X] .unpaged() 일 때
            total = content.size();
        }

        // 6. PageImpl 반환
        return new PageImpl<>(content, pageable, total);
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
                PathBuilder pathBuilder = new PathBuilder<>(systemDefEntity.getType(), systemDefEntity.getMetadata());

                orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
            }
        }

        // 기본 정렬 조건 (만약 정렬 조건이 없다면 id 내림차순)
        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(Order.DESC, systemDefEntity.id));
        }

        return orders.toArray(new OrderSpecifier[0]);
    }



    private BooleanExpression systemDefNameContains(String systemDefName) {
        return StringUtils.hasText(systemDefName) ? systemDefEntity.systemDefName.contains(systemDefName) : null;
    }
}
