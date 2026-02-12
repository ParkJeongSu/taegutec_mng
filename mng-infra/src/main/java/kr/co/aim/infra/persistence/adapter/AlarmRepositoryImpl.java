package kr.co.aim.infra.persistence.adapter;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.domain.model.Alarm;
import kr.co.aim.domain.repository.AlarmRepository;
import kr.co.aim.infra.persistence.entity.AlarmEntity;
import kr.co.aim.infra.persistence.mapper.AlarmMapper;
import kr.co.aim.infra.persistence.springdatajpa.AlarmHistoryJpaRepository;
import kr.co.aim.infra.persistence.springdatajpa.AlarmJpaRepository;
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
public class AlarmRepositoryImpl implements AlarmRepository {
    // Spring Data JPA가 자동으로 구현해주는 JPA 리포지토리. UserEntity를 다룬다.
    private final AlarmJpaRepository alarmJpaRepository;
    private final AlarmHistoryJpaRepository alarmHistoryJpaRepository;
    private final AlarmMapper alarmMapper;
    private final JPAQueryFactory queryFactory; // ✨ JPAQueryFactory 주입


    @Override
    public List<Alarm> findAll() {
        List<AlarmEntity> alarmEntities = alarmJpaRepository.findAll();
        return alarmEntities.stream().map(alarmMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<Alarm> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public Optional<Alarm> findByAlarmDefIdAndEquipmentName(Long alarmDefId, String equipmentName) {
        Optional<AlarmEntity> alarmEntityOptional = alarmJpaRepository.findByAlarmDefIdAndEquipmentName(alarmDefId,equipmentName).stream().findFirst();
        return alarmEntityOptional.map(alarmMapper::toDomain);
    }

    //@PublishHistoryEvent
    @Override
    public Alarm save(Alarm alarm) {
        AlarmEntity entity = alarmMapper.toEntity(alarm);
        AlarmEntity savedEntity = alarmJpaRepository.save(entity);
        //AlarmHistoryEntity historyEntity = alarmMapper.toHistoryEntity(alarm);
        //alarmHistoryJpaRepository.save(historyEntity);
        return alarmMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteAllByIdInBatch(List<Long> ids) {
        alarmJpaRepository.deleteAllByIdInBatch(ids);
    }


//    @Override
//    public Page<AlarmResponseDto> findAlarmWithConditions(AlarmSearchConditionDto condition, Pageable pageable) {
//
//        // 1. 공통 쿼리 빌더 생성 (SELECT, FROM, JOIN, WHERE)
//        JPAQuery<AlarmResponseDto> query = queryFactory
//                .select(new QAlarmResponseDto(
//                        alarmEntity.id,
//                        alarmEntity.alarmDefId,
//                        alarmDefEntity.alarmDefName,
//                        alarmEntity.equipmentName,
//                        alarmEntity.alarmState,
//                        alarmEntity.createTime,
//                        alarmEntity.clearTime,
//                        alarmEntity.eventName,
//                        alarmEntity.eventTime,
//                        alarmEntity.eventUser,
//                        alarmEntity.eventComment
//                ))
//                .from(alarmEntity)
//                .leftJoin(alarmDefEntity).on(alarmEntity.alarmDefId.eq(alarmDefEntity.id))
//                .where(
//                        alarmDefNameContains(condition.getAlarmDefName()),
//                        alarmStateEq(condition.getAlarmState()),
//                        equipmentNameContains(condition.getEquipmentName())
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
//        List<AlarmResponseDto> content = query.fetch();
//
//        // 5. 카운트 조회 (isPaged()로 분기)
//        long total;
//        if (pageable.isPaged()) {
//            // [페이징 O] 별도 카운트 쿼리 실행
//            Long count = queryFactory
//                    .select(alarmEntity.count())
//                    .from(alarmEntity)
//                    .leftJoin(alarmDefEntity).on(alarmEntity.alarmDefId.eq(alarmDefEntity.id)) // Count 쿼리에도 Join이 필요할 수 있음
//                    .where(
//                            alarmDefNameContains(condition.getAlarmDefName()),
//                            alarmStateEq(condition.getAlarmState()),
//                            equipmentNameContains(condition.getEquipmentName())
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
//                PathBuilder pathBuilder = new PathBuilder<>(alarmEntity.getType(), alarmEntity.getMetadata());
//
//                orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
//            }
//        }
//
//        // 기본 정렬 조건 (만약 정렬 조건이 없다면 id 내림차순)
//        if (orders.isEmpty()) {
//            orders.add(new OrderSpecifier(Order.DESC, alarmEntity.id));
//        }
//
//        return orders.toArray(new OrderSpecifier[0]);
//    }
//
//
//    // == 동적 쿼리를 위한 BooleanExpression 메소드들 ==
//    private BooleanExpression alarmDefNameContains(String alarmDefName) {
//        return StringUtils.hasText(alarmDefName) ? alarmDefEntity.alarmDefName.contains(alarmDefName) : null;
//    }
//
//    private BooleanExpression alarmStateEq(String alarmState) {
//        return StringUtils.hasText(alarmState) ? alarmEntity.alarmState.eq(alarmState) : null;
//    }
//
//    private BooleanExpression equipmentNameContains(String equipmentName) {
//        return StringUtils.hasText(equipmentName) ? alarmEntity.equipmentName.contains(equipmentName) : null;
//    }
}
