package kr.co.aim.infra.persistence.adapter;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.common.dto.QWorkOrderResponseDto;
import kr.co.aim.common.dto.WorkOrderResponseDto;
import kr.co.aim.common.dto.WorkOrderSearchConditionDto;
import kr.co.aim.domain.model.WorkOrder;
import kr.co.aim.domain.repository.WorkOrderRepository;
import kr.co.aim.infra.persistence.entity.WorkOrderEntity;
import kr.co.aim.infra.persistence.mapper.WorkOrderMapper;
import kr.co.aim.infra.persistence.springdatajpa.WorkOrderJpaRepository;
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

import static kr.co.aim.infra.persistence.entity.QWorkOrderEntity.workOrderEntity;

/**
 * UserRepository의 JPA 기반 구현체.
 * 실제 DB 작업은 Spring Data JPA가 제공하는 JpaRepository에 위임합니다.
 */

@Repository
@RequiredArgsConstructor
public class WorkOrderRepositoryImpl implements WorkOrderRepository {
    // Spring Data JPA가 자동으로 구현해주는 JPA 리포지토리. UserEntity를 다룬다.
    private final WorkOrderJpaRepository workOrderJpaRepository;
    private final WorkOrderMapper workOrderMapper;
    private final JPAQueryFactory queryFactory; // ✨ JPAQueryFactory 주입


    @Override
    public WorkOrder save(WorkOrder workOrder) {
        WorkOrderEntity entity = workOrderMapper.toEntity(workOrder);
        WorkOrderEntity savedEntity = workOrderJpaRepository.save(entity);
        return workOrderMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<WorkOrder> findById(Long id) {
        return workOrderJpaRepository.findById(id).map(workOrderMapper::toDomain);
    }

    @Override
    public Optional<WorkOrder> findByWorkOrderName(String workOrderName) {
        return workOrderJpaRepository.findByWorkOrderName(workOrderName).map(workOrderMapper::toDomain);
    }

    @Override
    public List<WorkOrder> findAll() {
        return workOrderJpaRepository.findAll().stream().map(workOrderMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteAllByIdInBatch(List<Long> ids) {
        workOrderJpaRepository.deleteAllByIdInBatch(ids);
    }

    @Override
    public Page<WorkOrderResponseDto> findWorkOrderWithConditions(WorkOrderSearchConditionDto condition, Pageable pageable) {
        // 1. 공통 쿼리 빌더 생성 (SELECT, FROM, JOIN, WHERE)
        JPAQuery<WorkOrderResponseDto> query = queryFactory
                .select(new QWorkOrderResponseDto(
                        workOrderEntity.id,
                        workOrderEntity.workOrderName,
                        workOrderEntity.description,
                        workOrderEntity.vendorName,
                        workOrderEntity.productDefName,
                        workOrderEntity.processFlowName,
                        workOrderEntity.processOperationName,
                        workOrderEntity.recipeName,
                        workOrderEntity.workOrderState,
                        workOrderEntity.holdState,
                        workOrderEntity.reasonCode,
                        workOrderEntity.equipmentName,
                        workOrderEntity.planQuantity,
                        workOrderEntity.createdQuantity,
                        workOrderEntity.releasedQuantity,
                        workOrderEntity.finishedQuantity,
                        workOrderEntity.scrappedQuantity,
                        workOrderEntity.workOrderCount,
                        workOrderEntity.createTime,
                        workOrderEntity.releaseTime,
                        workOrderEntity.completeTime,
                        workOrderEntity.createUser,
                        workOrderEntity.releaseUser,
                        workOrderEntity.completeUser,
                        workOrderEntity.dueDate,
                        workOrderEntity.eventName,
                        workOrderEntity.eventTime,
                        workOrderEntity.eventUser,
                        workOrderEntity.eventComment
                ))
                .from(workOrderEntity)
                .where(
                        // (WHERE 조건이 있다면 여기에 추가)
                        workOrderNameContains(condition.getWorkOrderName())
                );

        // 2. 정렬 적용
        query.orderBy(getOrderSpecifiers(pageable.getSort()));

        // 3. 페이징 적용 (isPaged()로 분기)
        if (pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        // 4. 데이터 조회
        List<WorkOrderResponseDto> content = query.fetch();

        // 5. 카운트 조회 (isPaged()로 분기)
        long total;
        if (pageable.isPaged()) {
            // [페이징 O] 별도 카운트 쿼리 실행
            Long count = queryFactory
                    .select(workOrderEntity.count())
                    .from(workOrderEntity)
                    .where(
                            // (WHERE 조건이 있다면 여기에 추가)
                            workOrderNameContains(condition.getWorkOrderName())
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

    @Override
    public Optional<WorkOrder> findByWorkOrderState(String workOrderState) {
        return workOrderJpaRepository.findByWorkOrderState(workOrderState).map(workOrderMapper::toDomain);
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
                PathBuilder pathBuilder = new PathBuilder<>(workOrderEntity.getType(), workOrderEntity.getMetadata());

                orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
            }
        }

        // 기본 정렬 조건 (만약 정렬 조건이 없다면 id 내림차순)
        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(Order.DESC, workOrderEntity.id));
        }

        return orders.toArray(new OrderSpecifier[0]);
    }

    private BooleanExpression workOrderNameContains(String workOrderName) {
        return StringUtils.hasText(workOrderName) ? workOrderEntity.workOrderName.contains(workOrderName) : null;
    }
}
