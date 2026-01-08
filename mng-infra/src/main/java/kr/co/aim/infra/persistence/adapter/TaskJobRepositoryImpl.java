package kr.co.aim.infra.persistence.adapter;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.common.dto.*;
import kr.co.aim.domain.model.TaskJob;
import kr.co.aim.domain.repository.TaskJobRepository;
import kr.co.aim.infra.persistence.entity.TaskJobEntity;
import kr.co.aim.infra.persistence.mapper.TaskJobMapper;
import kr.co.aim.infra.persistence.springdatajpa.TaskJobJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static kr.co.aim.infra.persistence.entity.QTaskJobEntity.taskJobEntity;

/**
 * UserRepository의 JPA 기반 구현체.
 * 실제 DB 작업은 Spring Data JPA가 제공하는 JpaRepository에 위임합니다.
 */

@Repository
@RequiredArgsConstructor
public class TaskJobRepositoryImpl implements TaskJobRepository {
    private final TaskJobJpaRepository taskJobJpaRepository;
    private final TaskJobMapper taskJobMapper;
    private final JPAQueryFactory queryFactory; // ✨ JPAQueryFactory 주입

    @Override
    public TaskJob save(TaskJob taskJob) {
        TaskJobEntity entity = taskJobMapper.toEntity(taskJob);
        TaskJobEntity savedEntity = taskJobJpaRepository.save(entity);
        return taskJobMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<TaskJob> findById(Long id) {
        return taskJobJpaRepository.findById(id).map(taskJobMapper::toDomain);
    }

    @Override
    public List<TaskJob> findAll() {
        return taskJobJpaRepository.findAll().stream().map(taskJobMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteAllByIdInBatch(List<Long> ids) {
        taskJobJpaRepository.deleteAllByIdInBatch(ids);
    }

    @Override
    public Optional<TaskJob> findByEquipmentName(String equipmentName) {
        return taskJobJpaRepository.findByEquipmentName(equipmentName).map(taskJobMapper::toDomain);
    }

    @Override
    public Optional<TaskJob> findByTaskState(String taskState) {
        return taskJobJpaRepository.findByTaskState(taskState).map(taskJobMapper::toDomain);
    }

    @Override
    public Page<TaskJobResponseDto> findTaskJobWithConditions(TaskJobSearchConditionDto condition, Pageable pageable) {
        // 1. 공통 쿼리 빌더 생성 (SELECT, FROM, JOIN, WHERE)
        JPAQuery<TaskJobResponseDto> query = queryFactory
                .select(new QTaskJobResponseDto(
                                taskJobEntity.id,
                                taskJobEntity.taskName,
                                taskJobEntity.taskType,
                                taskJobEntity.equipmentName,
                                taskJobEntity.taskGroupName,
                                taskJobEntity.step,
                                taskJobEntity.workOrderId,
                                taskJobEntity.taskState,
                                taskJobEntity.carrierCount,
                                taskJobEntity.transportTryCount,
                                taskJobEntity.recipeName,
                                taskJobEntity.createTime,
                                taskJobEntity.departedTime,
                                taskJobEntity.arrivedTime,
                                taskJobEntity.startTime,
                                taskJobEntity.completedTime,
                                taskJobEntity.eventName,
                                taskJobEntity.eventTime,
                                taskJobEntity.eventUser,
                                taskJobEntity.eventComment
                ))
                .from(taskJobEntity)
                .where(
                        // (WHERE 조건이 있다면 여기에 추가)
                );

        // 2. 정렬 적용
        query.orderBy(getOrderSpecifiers(pageable.getSort()));

        // 3. 페이징 적용 (isPaged()로 분기)
        if (pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        // 4. 데이터 조회
        List<TaskJobResponseDto> content = query.fetch();

        // 5. 카운트 조회 (isPaged()로 분기)
        long total;
        if (pageable.isPaged()) {
            // [페이징 O] 별도 카운트 쿼리 실행
            Long count = queryFactory
                    .select(taskJobEntity.count())
                    .from(taskJobEntity)
                    .where(
                            // (WHERE 조건이 있다면 여기에 추가)
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

    private OrderSpecifier<?>[] getOrderSpecifiers(Sort sort) {
        List<OrderSpecifier> orders = new ArrayList<>();

        if (sort.isSorted()) {
            for (Sort.Order order : sort) {
                // 정렬 방향을 결정합니다 (ASC or DESC)
                Order direction = order.isAscending() ? Order.ASC : Order.DESC;

                // 정렬할 속성(컬럼)을 PathBuilder를 통해 지정합니다.
                // "userName"과 같은 문자열을 Q-Type 경로로 변환해줍니다.
                PathBuilder pathBuilder = new PathBuilder<>(taskJobEntity.getType(), taskJobEntity.getMetadata());

                orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
            }
        }

        // 기본 정렬 조건 (만약 정렬 조건이 없다면 id 내림차순)
        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(Order.DESC, taskJobEntity.id));
        }

        return orders.toArray(new OrderSpecifier[0]);
    }
}
