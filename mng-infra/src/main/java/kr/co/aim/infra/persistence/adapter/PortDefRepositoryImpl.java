package kr.co.aim.infra.persistence.adapter;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.domain.model.PortDef;
import kr.co.aim.domain.repository.PortDefRepository;
import kr.co.aim.infra.persistence.entity.PortDefEntity;
import kr.co.aim.infra.persistence.mapper.PortDefMapper;
import kr.co.aim.infra.persistence.springdatajpa.PortDefJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * UserRepository의 JPA 기반 구현체.
 * 실제 DB 작업은 Spring Data JPA가 제공하는 JpaRepository에 위임합니다.
 */

@Repository
@RequiredArgsConstructor
public class PortDefRepositoryImpl implements PortDefRepository {
    // Spring Data JPA가 자동으로 구현해주는 JPA 리포지토리. UserEntity를 다룬다.

    private final PortDefJpaRepository portDefJpaRepository;
    private final PortDefMapper portDefMapper;
    private final JPAQueryFactory queryFactory; // ✨ JPAQueryFactory 주입

    @Override
    public List<PortDef> findAll() {
        return portDefJpaRepository.findAll().stream().map(portDefMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<PortDef> findByEquipmentNameAndPortName(String equipmentName, String portName) {
        return portDefJpaRepository.findByEquipmentNameAndPortName(equipmentName,portName).map(portDefMapper::toDomain);
    }

    @Override
    public PortDef save(PortDef portDef) {
        PortDefEntity entity = portDefMapper.toEntity(portDef);
        PortDefEntity savedEntity = portDefJpaRepository.save(entity);
        return portDefMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<PortDef> findByLocationId(String locationId) {
        return portDefJpaRepository.findByLocationId(locationId).map(portDefMapper::toDomain);
    }

//    @Override
//    public Page<PortDefResponseDto> findPortDefWithConditions(PortDefSearchConditionDto condition, Pageable pageable) {
//
//        // 1. 공통 쿼리 빌더 생성 (SELECT, FROM, WHERE)
//        JPAQuery<PortDefResponseDto> query = queryFactory
//                .select(new QPortDefResponseDto(
//                            portDefEntity.id,
//                            portDefEntity.equipmentName,
//                            portDefEntity.portName,
//                            portDefEntity.description,
//                            portDefEntity.portType,
//                            portDefEntity.portUseType,
//                            portDefEntity.containerType,
//                            portDefEntity.checkOutState,
//                            portDefEntity.checkOutTime,
//                            portDefEntity.checkOutUser,
//                            portDefEntity.dataState,
//                            portDefEntity.eventName,
//                            portDefEntity.eventTime,
//                            portDefEntity.eventUser,
//                            portDefEntity.eventComment
//                ))
//                .from(portDefEntity)
//                .where(
//                        // (WHERE 조건이 있다면 여기에 추가)
//                );
//
//        // 2. 정렬 적용
//        query.orderBy(getOrderSpecifiers(pageable.getSort()));
//
//        // 3. 페이징 적용 (isPaged()로 분기)
//        if (pageable.isPaged()) {
//            query.offset(pageable.getOffset());
//            query.limit(pageable.getPageSize());
//        }
//
//        // 4. 데이터 조회
//        List<PortDefResponseDto> content = query.fetch();
//
//        // 5. 카운트 조회 (isPaged()로 분기)
//        long total;
//        if (pageable.isPaged()) {
//            // [페이징 O] 별도 카운트 쿼리 실행
//            Long count = queryFactory
//                    .select(portDefEntity.count())
//                    .from(portDefEntity)
//                    .where(
//                            // (WHERE 조건이 있다면 여기에 추가)
//                    )
//                    .fetchOne();
//
//            total = (count != null) ? count.longValue() : 0L;
//
//        } else {
//            // [페이징 X] .unpaged() 일 때
//            total = content.size();
//        }
//
//        // 6. PageImpl 반환
//        return new PageImpl<>(content, pageable, total);
//    }
//
//    /**
//     * Pageable의 Sort 객체를 Querydsl의 OrderSpecifier 배열로 변환합니다.
//     */
//    private OrderSpecifier<?>[] getOrderSpecifiers(Sort sort) {
//        List<OrderSpecifier> orders = new ArrayList<>();
//
//        if (sort.isSorted()) {
//            for (Sort.Order order : sort) {
//                // 정렬 방향을 결정합니다 (ASC or DESC)
//                Order direction = order.isAscending() ? Order.ASC : Order.DESC;
//
//                // 정렬할 속성(컬럼)을 PathBuilder를 통해 지정합니다.
//                // "userName"과 같은 문자열을 Q-Type 경로로 변환해줍니다.
//                PathBuilder pathBuilder = new PathBuilder<>(portDefEntity.getType(), portDefEntity.getMetadata());
//
//                orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
//            }
//        }
//
//        // 기본 정렬 조건 (만약 정렬 조건이 없다면 id 내림차순)
//        if (orders.isEmpty()) {
//            orders.add(new OrderSpecifier(Order.DESC, portDefEntity.id));
//        }
//
//        return orders.toArray(new OrderSpecifier[0]);
//    }

}
