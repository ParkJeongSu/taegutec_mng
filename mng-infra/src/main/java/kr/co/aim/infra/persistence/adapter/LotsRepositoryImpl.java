package kr.co.aim.infra.persistence.adapter;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.common.dto.LotsResponseDto;
import kr.co.aim.common.dto.LotsSearchConditionDto;
import kr.co.aim.common.dto.QLotsResponseDto;
import kr.co.aim.domain.model.Lots;
import kr.co.aim.domain.repository.LotsRepository;
import kr.co.aim.infra.persistence.entity.LotsEntity;
import kr.co.aim.infra.persistence.mapper.LotsMapper;
import kr.co.aim.infra.persistence.springdatajpa.LotsJpaRepository;
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

import static kr.co.aim.infra.persistence.entity.QLotsEntity.lotsEntity;

/**
 * UserRepository의 JPA 기반 구현체.
 * 실제 DB 작업은 Spring Data JPA가 제공하는 JpaRepository에 위임합니다.
 */

@Repository
@RequiredArgsConstructor
public class LotsRepositoryImpl implements LotsRepository {
    // Spring Data JPA가 자동으로 구현해주는 JPA 리포지토리. UserEntity를 다룬다.
    private final LotsJpaRepository lotsJpaRepository;
    private final LotsMapper lotsMapper;
    private final JPAQueryFactory queryFactory; // ✨ JPAQueryFactory 주입

    @Override
    public Lots save(Lots lots) {
        // 1. Domain -> Entity 변환
        LotsEntity entity = lotsMapper.toEntity(lots);
        // 2. JPA 리포지토리를 통해 DB에 저장
        LotsEntity savedEntity = lotsJpaRepository.save(entity);
        // 3. 저장된 Entity -> Domain 변환 후 반환
        return lotsMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Lots> findById(Long id) {
        // 1. JPA 리포지토리를 통해 ID로 Entity 조회
        Optional<LotsEntity> entityOptional = lotsJpaRepository.findById(id);
        // 2. 조회된 Optional<Entity>를 Optional<Domain>으로 변환하여 반환
        return entityOptional.map(lotsMapper::toDomain);
    }

    @Override
    public Optional<Lots> findByLotName(String carrierName) {
        return lotsJpaRepository.findByLotName(carrierName).map(lotsMapper::toDomain);
    }

    @Override
    public List<Lots> findAll() {
        // 1. JPA 리포지토리를 통해 모든 UserEntity 조회
        List<LotsEntity> entities = lotsJpaRepository.findAll();
        // 2. Entity 리스트를 Domain 객체 리스트로 변환하여 반환
        return entities.stream()
                .map(lotsMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Lots> findByCarrierId(Long carrierId) {
        return lotsJpaRepository.findByCarrierId(carrierId).stream().map(lotsMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteAllByIdInBatch(List<Long> ids) {
        lotsJpaRepository.deleteAllByIdInBatch(ids);
    }

    @Override
    public Page<LotsResponseDto> findLotsWithConditions(LotsSearchConditionDto condition, Pageable pageable) {

        // 1. 공통 쿼리 빌더 생성 (SELECT, FROM, JOIN, WHERE)
        JPAQuery<LotsResponseDto> query = queryFactory
                .select(new QLotsResponseDto(
                            lotsEntity.id,
                            lotsEntity.lotName,
                            lotsEntity.productionType,
                            lotsEntity.lotState,
                            lotsEntity.processState,
                            lotsEntity.productDefId,
                            lotsEntity.processSpecId,
                            lotsEntity.processSpecVersion,
                            lotsEntity.processFlowId,
                            lotsEntity.processOperationId,
                            lotsEntity.workOrderId,
                            lotsEntity.equipmentName,
                            lotsEntity.portName,
                            lotsEntity.recipeName,
                            lotsEntity.carrierId,
                            lotsEntity.priority,
                            lotsEntity.lotGrade,
                            lotsEntity.productionDetailType,
                            lotsEntity.planStartDate,
                            lotsEntity.planDueDate,
                            lotsEntity.createTime,
                            lotsEntity.releaseTime,
                            lotsEntity.shipTime,
                            lotsEntity.trackInTime,
                            lotsEntity.trackOutTime,
                            lotsEntity.operationMoveTime,
                            lotsEntity.quantity,
                            lotsEntity.oldQuantity,
                            lotsEntity.holdState,
                            lotsEntity.reworkState,
                            lotsEntity.reworkCount,
                            lotsEntity.originalProcessSpecId,
                            lotsEntity.originalProcessSpecVersion,
                            lotsEntity.returnProcessFlowId,
                            lotsEntity.returnProcessOperationId,
                            lotsEntity.reasonCode,
                            lotsEntity.ownerCode,
                            lotsEntity.eventName,
                            lotsEntity.eventTime,
                            lotsEntity.eventUser,
                            lotsEntity.eventComment
                ))
                .from(lotsEntity)
                .where(
                        // (WHERE 조건이 있다면 여기에 추가)
                        lotNameContains(condition.getLotName())
                );

        // 2. 정렬 적용
        query.orderBy(getOrderSpecifiers(pageable.getSort()));

        // 3. 페이징 적용 (isPaged()로 분기)
        if (pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        // 4. 데이터 조회
        List<LotsResponseDto> content = query.fetch();

        // 5. 카운트 조회 (isPaged()로 분기)
        long total;
        if (pageable.isPaged()) {
            // [페이징 O] 별도 카운트 쿼리 실행
            Long count = queryFactory
                    .select(lotsEntity.count())
                    .from(lotsEntity)
                    .where(
                            // (WHERE 조건이 있다면 여기에 추가)
                            lotNameContains(condition.getLotName())
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
                PathBuilder pathBuilder = new PathBuilder<>(lotsEntity.getType(), lotsEntity.getMetadata());

                orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
            }
        }

        // 기본 정렬 조건 (만약 정렬 조건이 없다면 id 내림차순)
        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(Order.DESC, lotsEntity.id));
        }

        return orders.toArray(new OrderSpecifier[0]);
    }

    private BooleanExpression lotNameContains(String lotName) {
        return StringUtils.hasText(lotName) ? lotsEntity.lotName.contains(lotName) : null;
    }
}
