package kr.co.aim.infra.persistence.adapter;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.domain.model.Port;
import kr.co.aim.domain.repository.PortRepository;
import kr.co.aim.infra.persistence.entity.PortEntity;
import kr.co.aim.infra.persistence.mapper.PortMapper;
import kr.co.aim.infra.persistence.springdatajpa.PortJpaRepository;
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
public class PortRepositoryImpl implements PortRepository {
    // Spring Data JPA가 자동으로 구현해주는 JPA 리포지토리. UserEntity를 다룬다.
    private final PortJpaRepository portJpaRepository;
    private final PortMapper portMapper;
    private final JPAQueryFactory queryFactory; // ✨ JPAQueryFactory 주입


    @Override
    public Port save(Port port) {
        // 1. Domain -> Entity 변환
        PortEntity entity = portMapper.toEntity(port);
        // 2. JPA 리포지토리를 통해 DB에 저장
        PortEntity savedEntity = portJpaRepository.save(entity);
        // 3. 저장된 Entity -> Domain 변환 후 반환
        return portMapper.toDomain(savedEntity);
    }

    @Override
    public List<Port> findAll() {
        return portJpaRepository.findAll().stream().map(portMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<Port> findById(Long id) {
        return portJpaRepository.findById(id).map(portMapper::toDomain);
    }

    @Override
    public Optional<Port> findByEquipmentNameAndPortName(String equipmentName, String portName) {
        return portJpaRepository.findByEquipmentNameAndPortName(equipmentName,portName).map(portMapper::toDomain);
    }

    @Override
    public Optional<Port> findWithLockByEquipmentNameAndPortName(String equipmentName, String portName) {
        return portJpaRepository.findWithLockByEquipmentNameAndPortName(equipmentName,portName).map(portMapper::toDomain);
    }

    @Override
    public void deleteAllByIdInBatch(List<Long> ids) {
        portJpaRepository.deleteAllByIdInBatch(ids);
    }

//    @Override
//    public Page<PortsResponseDto> findPortsWithConditions(PortsSearchConditionDto condition, Pageable pageable) {
//
//        // 1. 공통 쿼리 빌더 생성 (SELECT, FROM, JOIN, WHERE)
//        JPAQuery<PortsResponseDto> query = queryFactory
//                .select(new QPortsResponseDto(
//                                portsEntity.id,
//                                portsEntity.equipmentName,
//                                portsEntity.portName,
//                                portsEntity.description,
//                                portsEntity.connectedStocker,
//                                portsEntity.transportMode,
//                                portsEntity.portState,
//                                portsEntity.resourceState,
//                                portsEntity.transportState,
//                                portsEntity.carrierName,
//                                portsEntity.transportJobId,
//                                portsEntity.eventName,
//                                portsEntity.eventTime,
//                                portsEntity.eventUser,
//                                portsEntity.eventComment
//                ))
//                .from(portsEntity)
//                .leftJoin(portDefEntity).on(portsEntity.equipmentName.eq(portDefEntity.equipmentName).and(portsEntity.portName.eq(portDefEntity.portName)))
//                .where(
//                        // (WHERE 조건이 있다면 여기에 추가)
//                        equipmentNameContains(condition.getEquipmentName()),
//                        portNameContains(condition.getPortName())
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
//        List<PortsResponseDto> content = query.fetch();
//
//        // 5. 카운트 조회 (isPaged()로 분기)
//        long total;
//        if (pageable.isPaged()) {
//            // [페이징 O] 별도 카운트 쿼리 실행
//            Long count = queryFactory
//                    .select(portsEntity.count())
//                    .from(portsEntity)
//                    .leftJoin(portDefEntity).on(portsEntity.equipmentName.eq(portDefEntity.equipmentName).and(portsEntity.portName.eq(portDefEntity.portName)))
//                    .where(
//                            // (WHERE 조건이 있다면 여기에 추가)
//                            equipmentNameContains(condition.getEquipmentName()),
//                            portNameContains(condition.getPortName())
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
//                PathBuilder pathBuilder = new PathBuilder<>(portsEntity.getType(), portsEntity.getMetadata());
//
//                orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
//            }
//        }
//
//        // 기본 정렬 조건 (만약 정렬 조건이 없다면 id 내림차순)
//        if (orders.isEmpty()) {
//            orders.add(new OrderSpecifier(Order.DESC, portsEntity.id));
//        }
//
//        return orders.toArray(new OrderSpecifier[0]);
//    }
//
//    // == 동적 쿼리를 위한 BooleanExpression 메소드들 ==
//
//    private BooleanExpression equipmentNameContains(String equipmentName) {
//        return StringUtils.hasText(equipmentName) ? portsEntity.equipmentName.contains(equipmentName) : null;
//    }
//
//    private BooleanExpression portNameContains(String portName) {
//        return StringUtils.hasText(portName) ? portsEntity.portName.contains(portName) : null;
//    }

}
