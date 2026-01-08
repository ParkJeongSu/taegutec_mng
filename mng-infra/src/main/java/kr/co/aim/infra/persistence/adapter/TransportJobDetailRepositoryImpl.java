package kr.co.aim.infra.persistence.adapter;


import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.common.dto.*;
import kr.co.aim.domain.model.TransportJobDetail;
import kr.co.aim.domain.repository.TransportJobDetailRepository;
import kr.co.aim.infra.persistence.entity.TransportJobDetailEntity;
import kr.co.aim.infra.persistence.mapper.TransportJobDetailMapper;
import kr.co.aim.infra.persistence.springdatajpa.TransportJobDetailJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static kr.co.aim.infra.persistence.entity.QTransportJobDetailEntity.transportJobDetailEntity;
import static kr.co.aim.infra.persistence.entity.QTransportJobEntity.transportJobEntity;

/**
 * UserRepository의 JPA 기반 구현체.
 * 실제 DB 작업은 Spring Data JPA가 제공하는 JpaRepository에 위임합니다.
 */

@Repository
@RequiredArgsConstructor
public class TransportJobDetailRepositoryImpl implements TransportJobDetailRepository {
    // Spring Data JPA가 자동으로 구현해주는 JPA 리포지토리. UserEntity를 다룬다.
    private final TransportJobDetailJpaRepository transportJobDetailJpaRepository;
    private final TransportJobDetailMapper transportJobDetailMapper;
    private final JPAQueryFactory queryFactory; // ✨ JPAQueryFactory 주입


    @Override
    public TransportJobDetail save(TransportJobDetail transportJob) {
        // 1. Domain -> Entity 변환
        TransportJobDetailEntity entity = transportJobDetailMapper.toEntity(transportJob);
        // 2. JPA 리포지토리를 통해 DB에 저장
        TransportJobDetailEntity savedEntity = transportJobDetailJpaRepository.save(entity);
        // 3. 저장된 Entity -> Domain 변환 후 반환
        return transportJobDetailMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<TransportJobDetail> findById(Long id) {
        // 1. JPA 리포지토리를 통해 ID로 Entity 조회
        Optional<TransportJobDetailEntity> entityOptional = transportJobDetailJpaRepository.findById(id);
        // 2. 조회된 Optional<Entity>를 Optional<Domain>으로 변환하여 반환
        return entityOptional.map(transportJobDetailMapper::toDomain);
    }

    @Override
    public Optional<TransportJobDetail> findByTransportJobDetailName(String transportJobDetailName) {
        return transportJobDetailJpaRepository.findByTransportJobDetailName(transportJobDetailName).map(transportJobDetailMapper::toDomain);
    }

    @Override
    public List<TransportJobDetail> findByTransportJobId(Long transportJobId) {
        return transportJobDetailJpaRepository.findByTransportJobId(transportJobId)
                .stream()
                .map(transportJobDetailMapper::toDomain)
                .collect(Collectors.toList());
    }


    @Override
    public List<TransportJobDetail> findAll() {
        // 1. JPA 리포지토리를 통해 모든 UserEntity 조회
        List<TransportJobDetailEntity> entities = transportJobDetailJpaRepository.findAll();
        // 2. Entity 리스트를 Domain 객체 리스트로 변환하여 반환
        return entities.stream()
                .map(transportJobDetailMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAllByIdInBatch(List<Long> ids) {
        transportJobDetailJpaRepository.deleteAllByIdInBatch(ids);
    }

    @Override
    public Page<TransportJobDetailResponseDto> findTransportJobDetailWithConditions(TransportJobDetailSearchConditionDto condition, Pageable pageable) {
        // 1. 공통 쿼리 빌더 생성 (SELECT, FROM, JOIN, WHERE)
        JPAQuery<TransportJobDetailResponseDto> query = queryFactory
                .select(new QTransportJobDetailResponseDto(
                            transportJobDetailEntity.id,
                            transportJobDetailEntity.transportJobDetailName,
                            transportJobDetailEntity.transportJobId,
                            transportJobDetailEntity.transportJobDetailState,
                            transportJobDetailEntity.carrierId,
                            transportJobDetailEntity.sourceEquipmentName,
                            transportJobDetailEntity.sourcePortName,
                            transportJobDetailEntity.sourceZoneName,
                            transportJobDetailEntity.sourceShelfName,
                            transportJobDetailEntity.destinationEquipmentName,
                            transportJobDetailEntity.destinationPortName,
                            transportJobDetailEntity.destinationZoneName,
                            transportJobDetailEntity.destinationShelfName,
                            transportJobDetailEntity.currentEquipmentName,
                            transportJobDetailEntity.currentPortName,
                            transportJobDetailEntity.currentZoneName,
                            transportJobDetailEntity.currentShelfName,
                            transportJobDetailEntity.stepOrder,
                            transportJobDetailEntity.stepPhase,
                            transportJobDetailEntity.eventName,
                            transportJobDetailEntity.eventTime,
                            transportJobDetailEntity.eventUser,
                            transportJobDetailEntity.eventComment
                ))
                .from(transportJobDetailEntity)
                .where(
                        // (WHERE 조건이 있다면 여기에 추가)
                        transportJobNameContains(condition.getTransportJobId())
                );

        // 2. 정렬 적용
        query.orderBy(getOrderSpecifiers(pageable.getSort()));

        // 3. 페이징 적용 (isPaged()로 분기)
        if (pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        // 4. 데이터 조회
        List<TransportJobDetailResponseDto> content = query.fetch();

        // 5. 카운트 조회 (isPaged()로 분기)
        long total;
        if (pageable.isPaged()) {
            // [페이징 O] 별도 카운트 쿼리 실행
            Long count = queryFactory
                    .select(transportJobDetailEntity.count())
                    .from(transportJobDetailEntity)
                    .where(
                            // (WHERE 조건이 있다면 여기에 추가)
                            transportJobNameContains(condition.getTransportJobId())
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
                PathBuilder pathBuilder = new PathBuilder<>(transportJobDetailEntity.getType(), transportJobDetailEntity.getMetadata());

                orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
            }
        }

        // 기본 정렬 조건 (만약 정렬 조건이 없다면 id 내림차순)
        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(Order.DESC, transportJobDetailEntity.id));
        }

        return orders.toArray(new OrderSpecifier[0]);
    }

    private BooleanExpression transportJobNameContains(Long transportJobId) {
        return transportJobId != null ? transportJobDetailEntity.transportJobId.eq(transportJobId) : null;
    }
}
