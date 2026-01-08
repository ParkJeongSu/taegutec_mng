package kr.co.aim.infra.persistence.adapter;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.common.dto.*;
import kr.co.aim.domain.model.TaskJobDetail;
import kr.co.aim.domain.repository.TaskJobDetailRepository;
import kr.co.aim.infra.persistence.entity.TaskJobDetailEntity;
import kr.co.aim.infra.persistence.mapper.TaskJobDetailMapper;
import kr.co.aim.infra.persistence.springdatajpa.TaskJobDetailJpaRepository;
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

import static kr.co.aim.infra.persistence.entity.QTaskJobDetailEntity.taskJobDetailEntity;

/**
 * UserRepository의 JPA 기반 구현체.
 * 실제 DB 작업은 Spring Data JPA가 제공하는 JpaRepository에 위임합니다.
 */

@Repository
@RequiredArgsConstructor
public class TaskJobDetailRepositoryImpl implements TaskJobDetailRepository {
    private final TaskJobDetailJpaRepository taskJobDetailJpaRepository;
    private final TaskJobDetailMapper taskJobDetailMapper;
    private final JPAQueryFactory queryFactory; // ✨ JPAQueryFactory 주입

    @Override
    public TaskJobDetail save(TaskJobDetail taskJobDetail) {
        TaskJobDetailEntity entity = taskJobDetailMapper.toEntity(taskJobDetail);
        TaskJobDetailEntity savedEntity = taskJobDetailJpaRepository.save(entity);
        return taskJobDetailMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<TaskJobDetail> findById(Long id) {
        return taskJobDetailJpaRepository.findById(id).map(taskJobDetailMapper::toDomain);
    }

    @Override
    public List<TaskJobDetail> findAll() {
        return taskJobDetailJpaRepository.findAll().stream().map(taskJobDetailMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteAllByIdInBatch(List<Long> ids) {
        taskJobDetailJpaRepository.deleteAllByIdInBatch(ids);
    }

    @Override
    public List<TaskJobDetail> findByWipName(String wipName) {
        return taskJobDetailJpaRepository.findByWipName(wipName).stream().map(taskJobDetailMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<TaskJobDetail> findByCarrierNameAndState(String carrierName, String state) {
        return taskJobDetailJpaRepository.findByCarrierNameAndState(carrierName, state).stream().map(taskJobDetailMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Page<TaskJobDetailResponseDto> findTaskJobDetailWithConditions(TaskJobDetailSearchConditionDto condition, Pageable pageable) {
        // 1. 공통 쿼리 빌더 생성 (SELECT, FROM, JOIN, WHERE)
        JPAQuery<TaskJobDetailResponseDto> query = queryFactory
                .select(new QTaskJobDetailResponseDto(
                                taskJobDetailEntity.id,
                                taskJobDetailEntity.taskJobId,
                                taskJobDetailEntity.wipName,
                                taskJobDetailEntity.carrierName,
                                taskJobDetailEntity.state,
                                taskJobDetailEntity.createTime,
                                taskJobDetailEntity.departedTime,
                                taskJobDetailEntity.arrivedTime,
                                taskJobDetailEntity.startTime,
                                taskJobDetailEntity.completedTime,
                                taskJobDetailEntity.eventName,
                                taskJobDetailEntity.eventTime,
                                taskJobDetailEntity.eventUser,
                                taskJobDetailEntity.eventComment
                ))
                .from(taskJobDetailEntity)
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
        List<TaskJobDetailResponseDto> content = query.fetch();

        // 5. 카운트 조회 (isPaged()로 분기)
        long total;
        if (pageable.isPaged()) {
            // [페이징 O] 별도 카운트 쿼리 실행
            Long count = queryFactory
                    .select(taskJobDetailEntity.count())
                    .from(taskJobDetailEntity)
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
                PathBuilder pathBuilder = new PathBuilder<>(taskJobDetailEntity.getType(), taskJobDetailEntity.getMetadata());

                orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
            }
        }

        // 기본 정렬 조건 (만약 정렬 조건이 없다면 id 내림차순)
        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(Order.DESC, taskJobDetailEntity.id));
        }

        return orders.toArray(new OrderSpecifier[0]);
    }
}
