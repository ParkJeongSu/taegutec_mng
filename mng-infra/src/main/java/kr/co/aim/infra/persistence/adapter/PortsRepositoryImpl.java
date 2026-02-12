package kr.co.aim.infra.persistence.adapter;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.common.dto.PortsResponseDto;
import kr.co.aim.common.dto.PortsSearchConditionDto;
import kr.co.aim.common.dto.QPortsResponseDto;
import kr.co.aim.domain.model.Ports;
import kr.co.aim.domain.repository.PortsRepository;
import kr.co.aim.infra.persistence.entity.PortsEntity;
import kr.co.aim.infra.persistence.mapper.PortsMapper;
import kr.co.aim.infra.persistence.springdatajpa.PortsJpaRepository;
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

import static kr.co.aim.infra.persistence.entity.QPortDefEntity.portDefEntity;
import static kr.co.aim.infra.persistence.entity.QPortsEntity.portsEntity;

/**
 * UserRepository의 JPA 기반 구현체.
 * 실제 DB 작업은 Spring Data JPA가 제공하는 JpaRepository에 위임합니다.
 */

@Repository
@RequiredArgsConstructor
public class PortsRepositoryImpl implements PortsRepository {
    // Spring Data JPA가 자동으로 구현해주는 JPA 리포지토리. UserEntity를 다룬다.
    private final PortsJpaRepository portsJpaRepository;
    private final PortsMapper portsMapper;
    private final JPAQueryFactory queryFactory; // ✨ JPAQueryFactory 주입


    @Override
    public Ports save(Ports ports) {
        // 1. Domain -> Entity 변환
        PortsEntity entity = portsMapper.toEntity(ports);
        // 2. JPA 리포지토리를 통해 DB에 저장
        PortsEntity savedEntity = portsJpaRepository.save(entity);
        // 3. 저장된 Entity -> Domain 변환 후 반환
        return portsMapper.toDomain(savedEntity);
    }

    @Override
    public List<Ports> findAll() {
        return portsJpaRepository.findAll().stream().map(portsMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<Ports> findById(Long id) {
        return portsJpaRepository.findById(id).map(portsMapper::toDomain);
    }

    @Override
    public Optional<Ports> findByEquipmentNameAndPortName(String equipmentName, String portName) {
        return portsJpaRepository.findByEquipmentNameAndPortName(equipmentName,portName).map(portsMapper::toDomain);
    }

    @Override
    public void deleteAllByIdInBatch(List<Long> ids) {
        portsJpaRepository.deleteAllByIdInBatch(ids);
    }

    @Override
    public Page<PortsResponseDto> findPortsWithConditions(PortsSearchConditionDto condition, Pageable pageable) {

        // 1. 공통 쿼리 빌더 생성 (SELECT, FROM, JOIN, WHERE)
        JPAQuery<PortsResponseDto> query = queryFactory
                .select(new QPortsResponseDto(
                                portsEntity.id,
                                portsEntity.equipmentName,
                                portsEntity.portName,
                                portsEntity.description,
                                portsEntity.connectedStocker,
                                portsEntity.transportMode,
                                portsEntity.portState,
                                portsEntity.resourceState,
                                portsEntity.transportState,
                                portsEntity.carrierName,
                                portsEntity.transportJobId,
                                portsEntity.eventName,
                                portsEntity.eventTime,
                                portsEntity.eventUser,
                                portsEntity.eventComment
                ))
                .from(portsEntity)
                .leftJoin(portDefEntity).on(portsEntity.equipmentName.eq(portDefEntity.equipmentName).and(portsEntity.portName.eq(portDefEntity.portName)))
                .where(
                        // (WHERE 조건이 있다면 여기에 추가)
                        equipmentNameContains(condition.getEquipmentName()),
                        portNameContains(condition.getPortName())
                );

        // 2. 정렬 적용
        query.orderBy(getOrderSpecifiers(pageable.getSort()));

        // 3. 페이징 적용 (isPaged()로 분기)
        if (pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        // 4. 데이터 조회
        List<PortsResponseDto> content = query.fetch();

        // 5. 카운트 조회 (isPaged()로 분기)
        long total;
        if (pageable.isPaged()) {
            // [페이징 O] 별도 카운트 쿼리 실행
            Long count = queryFactory
                    .select(portsEntity.count())
                    .from(portsEntity)
                    .leftJoin(portDefEntity).on(portsEntity.equipmentName.eq(portDefEntity.equipmentName).and(portsEntity.portName.eq(portDefEntity.portName)))
                    .where(
                            // (WHERE 조건이 있다면 여기에 추가)
                            equipmentNameContains(condition.getEquipmentName()),
                            portNameContains(condition.getPortName())
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
                PathBuilder pathBuilder = new PathBuilder<>(portsEntity.getType(), portsEntity.getMetadata());

                orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
            }
        }

        // 기본 정렬 조건 (만약 정렬 조건이 없다면 id 내림차순)
        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(Order.DESC, portsEntity.id));
        }

        return orders.toArray(new OrderSpecifier[0]);
    }

    // == 동적 쿼리를 위한 BooleanExpression 메소드들 ==

    private BooleanExpression equipmentNameContains(String equipmentName) {
        return StringUtils.hasText(equipmentName) ? portsEntity.equipmentName.contains(equipmentName) : null;
    }

    private BooleanExpression portNameContains(String portName) {
        return StringUtils.hasText(portName) ? portsEntity.portName.contains(portName) : null;
    }

}
