package kr.co.aim.infra.persistence.adapter;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.common.dto.AuthorityResponseDto;
import kr.co.aim.common.dto.AuthoritySearchConditionDto;
import kr.co.aim.common.dto.QAuthorityResponseDto;
import kr.co.aim.domain.model.Authority;
import kr.co.aim.domain.repository.AuthorityRepository;
import kr.co.aim.infra.persistence.entity.AuthorityEntity;
import kr.co.aim.infra.persistence.mapper.AuthorityMapper;
import kr.co.aim.infra.persistence.springdatajpa.AuthorityJpaRepository;
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

import static kr.co.aim.infra.persistence.entity.QAuthorityEntity.authorityEntity;

@Repository
@RequiredArgsConstructor
public class AuthorityRepositoryImpl implements AuthorityRepository {
    // Spring Data JPA가 자동으로 구현해주는 JPA 리포지토리. UserEntity를 다룬다.
    private final AuthorityJpaRepository authorityJpaRepository;
    private final AuthorityMapper authorityMapper;
    private final JPAQueryFactory queryFactory; // ✨ JPAQueryFactory 주입


    @Override
    public Authority save(Authority authority) {
        // 1. Domain -> Entity 변환
        AuthorityEntity entity = authorityMapper.toEntity(authority);
        // 2. JPA 리포지토리를 통해 DB에 저장
        AuthorityEntity savedEntity = authorityJpaRepository.save(entity);
        // 3. 저장된 Entity -> Domain 변환 후 반환
        return authorityMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Authority> findById(Long id) {
        // 1. JPA 리포지토리를 통해 ID로 Entity 조회
        Optional<AuthorityEntity> entityOptional = authorityJpaRepository.findById(id);
        // 2. 조회된 Optional<Entity>를 Optional<Domain>으로 변환하여 반환
        return entityOptional.map(authorityMapper::toDomain);
    }

    @Override
    public Optional<Authority> findByAuthorityName(String authorityName) {
        // 1. JPA 리포지토리를 통해 Email로 Entity 조회
        Optional<AuthorityEntity> entityOptional = authorityJpaRepository.findByAuthorityName(authorityName);
        // 2. 조회된 Optional<Entity>를 Optional<Domain>으로 변환하여 반환
        return entityOptional.map(authorityMapper::toDomain);
    }

    @Override
    public List<Authority> findAll() {
        // 1. JPA 리포지토리를 통해 모든 UserEntity 조회
        List<AuthorityEntity> entities = authorityJpaRepository.findAll();
        // 2. Entity 리스트를 Domain 객체 리스트로 변환하여 반환
        return entities.stream()
                .map(authorityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAllByIdInBatch(List<Long> ids) {
        authorityJpaRepository.deleteAllByIdInBatch(ids);

    }

    @Override
    public Page<AuthorityResponseDto> findAuthsWithConditions(AuthoritySearchConditionDto condition, Pageable pageable) {

        // 1. 공통 쿼리 빌더 생성 (SELECT, FROM, WHERE)
        JPAQuery<AuthorityResponseDto> query = queryFactory
                .select(
                        new QAuthorityResponseDto(
                                authorityEntity.id,
                                authorityEntity.authorityName,
                                authorityEntity.description,
                                authorityEntity.checkOutState,
                                authorityEntity.checkOutTime,
                                authorityEntity.checkOutUser,
                                authorityEntity.dataState,
                                authorityEntity.eventName,
                                authorityEntity.eventTime,
                                authorityEntity.eventUser,
                                authorityEntity.eventComment
                        )
                )
                .from(authorityEntity)
                .where(
                        authorityNameContains(condition.getAuthorityName())
                );

        // 2. 정렬 적용
        query.orderBy(getOrderSpecifiers(pageable.getSort()));

        // 3. 페이징 적용 (isPaged()로 분기)
        if (pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        // 4. 데이터 조회
        List<AuthorityResponseDto> content = query.fetch();

        // 5. 카운트 조회 (isPaged()로 분기)
        long total;
        if (pageable.isPaged()) {
            // [페이징 O] 별도 카운트 쿼리 실행
            Long count = queryFactory
                    .select(authorityEntity.count())
                    .from(authorityEntity)
                    .where(
                            authorityNameContains(condition.getAuthorityName())
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
                PathBuilder pathBuilder = new PathBuilder<>(authorityEntity.getType(), authorityEntity.getMetadata());

                orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
            }
        }

        // 기본 정렬 조건 (만약 정렬 조건이 없다면 id 내림차순)
        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(Order.DESC, authorityEntity.id));
        }

        return orders.toArray(new OrderSpecifier[0]);
    }



    private BooleanExpression authorityNameContains(String authorityName) {
        return StringUtils.hasText(authorityName) ? authorityEntity.authorityName.contains(authorityName) : null;
    }


}
