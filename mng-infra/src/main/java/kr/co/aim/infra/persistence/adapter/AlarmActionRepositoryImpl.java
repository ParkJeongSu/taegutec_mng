package kr.co.aim.infra.persistence.adapter;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.domain.model.AlarmAction;
import kr.co.aim.domain.repository.AlarmActionRepository;
import kr.co.aim.infra.persistence.entity.AlarmActionEntity;
import kr.co.aim.infra.persistence.mapper.AlarmActionMapper;
import kr.co.aim.infra.persistence.springdatajpa.AlarmActionJpaRepository;
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
public class AlarmActionRepositoryImpl implements AlarmActionRepository {
    // Spring Data JPA가 자동으로 구현해주는 JPA 리포지토리. UserEntity를 다룬다.
    private final AlarmActionJpaRepository alarmActionJpaRepository;
    private final AlarmActionMapper alarmActionMapper;
    private final JPAQueryFactory queryFactory; // ✨ JPAQueryFactory 주입

    @Override
    public List<AlarmAction> findAll() {
        List<AlarmActionEntity> alarmActionEntities = alarmActionJpaRepository.findAll();
        return alarmActionEntities.stream().map(alarmActionMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<AlarmAction> findById(Long id) {
        Optional<AlarmActionEntity> optionalAlarmAction = alarmActionJpaRepository.findById(id);
        return optionalAlarmAction.map(alarmActionMapper::toDomain);
    }

    @Override
    public List<AlarmAction> findByAlarmDefId(Long alarmDefId) {
        List<AlarmActionEntity> alarmActionEntities = alarmActionJpaRepository.findByAlarmDefId(alarmDefId);
        return alarmActionEntities.stream().map(alarmActionMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<AlarmAction> findByAlarmActionName(String alarmActionName) {
        return alarmActionJpaRepository.findByAlarmActionName(alarmActionName).map(alarmActionMapper::toDomain);
    }

    @Override
    public AlarmAction save(AlarmAction alarmAction) {
        AlarmActionEntity entity = alarmActionMapper.toEntity(alarmAction);
        AlarmActionEntity savedEntity = alarmActionJpaRepository.save(entity);
        return alarmActionMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteAllByIdInBatch(List<Long> ids) {
        alarmActionJpaRepository.deleteAllByIdInBatch(ids);
    }


//    @Override
//    public Page<AlarmActionResponseDto> findAlarmActionWithConditions(AlarmActionSearchConditionDto condition, Pageable pageable) {
//
//        // 1. 공통 쿼리 빌더 생성 (SELECT, FROM, JOIN, WHERE)
//        JPAQuery<AlarmActionResponseDto> query = queryFactory
//                .select(new QAlarmActionResponseDto(
//                        alarmActionEntity.id,
//                        alarmActionEntity.alarmActionName,
//                        alarmActionEntity.actionType,
//                        alarmActionEntity.alarmDefId,
//                        alarmDefEntity.alarmDefName,
//                        alarmActionEntity.description,
//                        alarmActionEntity.dataState,
//                        alarmActionEntity.checkOutState,
//                        alarmActionEntity.checkOutTime,
//                        alarmActionEntity.checkOutUser,
//                        alarmActionEntity.eventName,
//                        alarmActionEntity.eventTime,
//                        alarmActionEntity.eventUser,
//                        alarmActionEntity.eventComment
//                ))
//                .from(alarmActionEntity)
//                .leftJoin(alarmDefEntity).on(alarmActionEntity.alarmDefId.eq(alarmDefEntity.id))
//                .where(
//                        // (WHERE 조건이 있다면 여기에 추가)
//                        alarmActionNameContains(condition.getAlarmActionName())
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
//        List<AlarmActionResponseDto> content = query.fetch();
//
//        // 5. 카운트 조회 (isPaged()로 분기)
//        long total;
//        if (pageable.isPaged()) {
//            // [페이징 O] 별도 카운트 쿼리 실행
//            Long count = queryFactory
//                    .select(alarmActionEntity.count())
//                    .from(alarmActionEntity)
//                    .leftJoin(alarmDefEntity).on(alarmActionEntity.alarmDefId.eq(alarmDefEntity.id))
//                    .where(
//                            // (WHERE 조건이 있다면 여기에 추가)
//                            alarmActionNameContains(condition.getAlarmActionName())
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
//                PathBuilder pathBuilder = new PathBuilder<>(alarmActionEntity.getType(), alarmActionEntity.getMetadata());
//
//                orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
//            }
//        }
//
//        // 기본 정렬 조건 (만약 정렬 조건이 없다면 id 내림차순)
//        if (orders.isEmpty()) {
//            orders.add(new OrderSpecifier(Order.DESC, alarmActionEntity.id));
//        }
//
//        return orders.toArray(new OrderSpecifier[0]);
//    }
//
//    // == 동적 쿼리를 위한 BooleanExpression 메소드들 ==
//    private BooleanExpression alarmActionNameContains(String alarmActionName) {
//        return StringUtils.hasText(alarmActionName) ? alarmActionEntity.alarmActionName.contains(alarmActionName) : null;
//    }

}
