package kr.co.aim.infra.persistence.adapter;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.common.dto.CarrierDefSearchConditionDto;
import kr.co.aim.domain.model.CarrierDef;
import kr.co.aim.domain.repository.CarrierDefRepository;
import kr.co.aim.infra.persistence.entity.CarrierDefEntity;
import kr.co.aim.infra.persistence.mapper.CarrierDefMapper;
import kr.co.aim.infra.persistence.springdatajpa.CarrierDefJpaRepository;
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

import static kr.co.aim.infra.persistence.entity.QCarrierDefEntity.carrierDefEntity;

/**
 * UserRepository의 JPA 기반 구현체.
 * 실제 DB 작업은 Spring Data JPA가 제공하는 JpaRepository에 위임합니다.
 */

@Repository
@RequiredArgsConstructor
public class CarrierDefRepositoryImpl implements CarrierDefRepository {
    // Spring Data JPA가 자동으로 구현해주는 JPA 리포지토리. UserEntity를 다룬다.

    private final CarrierDefJpaRepository carrierDefJpaRepository;
    private final CarrierDefMapper carrierDefMapper;
    private final JPAQueryFactory queryFactory; // ✨ JPAQueryFactory 주입

    @Override
    public List<CarrierDef> findAll() {
        // 1. JPA 리포지토리를 통해 모든 UserEntity 조회
        List<CarrierDefEntity> entities = carrierDefJpaRepository.findAll();
        // 2. Entity 리스트를 Domain 객체 리스트로 변환하여 반환
        return entities.stream()
                .map(carrierDefMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CarrierDef> findById(Long id) {

        // 1. findById(id)를 통해 Optional<CarrierDefEntity>를 얻습니다.
        // Optional<CarrierDefEntity> entityOptional = carriersDefJpaRepository.findById(id);

        // 2. Optional의 map 메서드를 사용합니다.
        // - entityOptional에 값이 있으면: 내부의 CarrierDefEntity에 toDomain 메서드를 적용하고,
        //                               그 결과(CarrierDef)를 다시 Optional로 감싸서 반환합니다. -> Optional<CarrierDef>
        // - entityOptional에 값이 없으면: 비어있는 Optional을 그대로 반환합니다. -> Optional.empty()

        return carrierDefJpaRepository.findById(id).map(carrierDefMapper::toDomain);
    }

    @Override
    public Optional<CarrierDef> findByCarrierDefName(String carrierDefName) {
        return carrierDefJpaRepository.findByCarrierDefName(carrierDefName).map(carrierDefMapper::toDomain);
    }

    @Override
    public CarrierDef save(CarrierDef carrierDef) {
        CarrierDefEntity entity = carrierDefMapper.toEntity(carrierDef);
        CarrierDefEntity savedEntity = carrierDefJpaRepository.save(entity);
        return carrierDefMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteAllByIdInBatch(List<Long> ids) {
        carrierDefJpaRepository.deleteAllByIdInBatch(ids);
    }

    @Override
    public Page<CarrierDef> findCarrierDefWithConditions(CarrierDefSearchConditionDto condition, Pageable pageable) {
        //1. 공통 쿼리 빌더 생성 (SELECT, FROM, JOIN, WHERE)
        JPAQuery<CarrierDefEntity> query = queryFactory
                .selectFrom(carrierDefEntity)
                .where(
                        // (WHERE 조건이 있다면 여기에 추가)
                        carrierDefNameContains(condition.getCarrierDefName())
                );

        // 2. 정렬 적용
        query.orderBy(getOrderSpecifiers(pageable.getSort()));

        // 3. 페이징 적용 (isPaged()로 분기)
        if (pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        // 4. 데이터 조회
        List<CarrierDefEntity> content = query.fetch();

        List<CarrierDef> converted = content.stream().map(carrierDefMapper::toDomain).collect(Collectors.toList());

        // 5. 카운트 조회 (isPaged()로 분기)
        long total;
        if (pageable.isPaged()) {
            // [페이징 O] 별도 카운트 쿼리 실행
            Long count = queryFactory
                    .select(carrierDefEntity.count())
                    .from(carrierDefEntity)
                    .where(
                            // (WHERE 조건이 있다면 여기에 추가)
                            carrierDefNameContains(condition.getCarrierDefName())
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
                PathBuilder pathBuilder = new PathBuilder<>(carrierDefEntity.getType(), carrierDefEntity.getMetadata());

                orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
            }
        }

        // 기본 정렬 조건 (만약 정렬 조건이 없다면 id 내림차순)
        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(Order.DESC, carrierDefEntity.id));
        }

        return orders.toArray(new OrderSpecifier[0]);
    }

    // == 동적 쿼리를 위한 BooleanExpression 메소드들 ==


    private BooleanExpression carrierDefNameContains(String carrierDefName) {
        return StringUtils.hasText(carrierDefName) ? carrierDefEntity.carrierDefName.contains(carrierDefName) : null;
    }
}
