package kr.co.aim.infra.persistence.adapter;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.domain.model.AlarmMailActionDetail;
import kr.co.aim.domain.repository.AlarmMailActionDetailRepository;
import kr.co.aim.infra.persistence.entity.AlarmMailActionDetailEntity;
import kr.co.aim.infra.persistence.mapper.AlarmMailActionDetailMapper;
import kr.co.aim.infra.persistence.springdatajpa.AlarmMailActionDetailJpaRepository;
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
public class AlarmMailActionDetailRepositoryImpl implements AlarmMailActionDetailRepository {
    // Spring Data JPA가 자동으로 구현해주는 JPA 리포지토리. UserEntity를 다룬다.
    private final AlarmMailActionDetailJpaRepository alarmMailActionDetailJpaRepository;
    private final AlarmMailActionDetailMapper alarmMailActionDetailMapper;
    private final JPAQueryFactory queryFactory; // ✨ JPAQueryFactory 주입

    @Override
    public List<AlarmMailActionDetail> findAll() {
        List<AlarmMailActionDetailEntity> alarmMailActionDetails = alarmMailActionDetailJpaRepository.findAll();
        return alarmMailActionDetails.stream().map(alarmMailActionDetailMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<AlarmMailActionDetail> findById(Long id) {
        Optional<AlarmMailActionDetailEntity> optionalAlarmMailActionDetailEntity = alarmMailActionDetailJpaRepository.findById(id);
        return optionalAlarmMailActionDetailEntity.map(alarmMailActionDetailMapper::toDomain);
    }

    @Override
    public List<AlarmMailActionDetail> findByAlarmActionId(Long alarmActionId) {
        return alarmMailActionDetailJpaRepository.findByAlarmActionId(alarmActionId)
                .stream().map(alarmMailActionDetailMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<AlarmMailActionDetail> findByAlarmActionUserGroupId(Long alarmActionUserGroupId) {
        return alarmMailActionDetailJpaRepository.findByAlarmActionUserGroupId(alarmActionUserGroupId)
                .stream().map(alarmMailActionDetailMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<AlarmMailActionDetail> findByAlarmActionIdAndAlarmActionUserGroupId(Long alarmActionId, Long alarmActionUserGroupId) {
        return alarmMailActionDetailJpaRepository.findByAlarmActionIdAndAlarmActionUserGroupId(alarmActionId,alarmActionUserGroupId)
                .map(alarmMailActionDetailMapper::toDomain);
    }

    @Override
    public AlarmMailActionDetail save(AlarmMailActionDetail alarmMailActionDetail) {
        AlarmMailActionDetailEntity entity = alarmMailActionDetailMapper.toEntity(alarmMailActionDetail);
        AlarmMailActionDetailEntity savedEntity = alarmMailActionDetailJpaRepository.save(entity);
        return alarmMailActionDetailMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteAllByIdInBatch(List<Long> ids) {
        alarmMailActionDetailJpaRepository.deleteAllByIdInBatch(ids);
    }


//    @Override
//    public Page<AlarmActionDetailResponseDto> findAlarmMailActionDetailWithConditions(AlarmActionDetailSearchConditionDto condition, Pageable pageable) {
//
//        // 1. 공통 쿼리 빌더 생성 (SELECT, FROM, JOIN, WHERE)
//        JPAQuery<AlarmActionDetailResponseDto> query = queryFactory
//                .select(new QAlarmActionDetailResponseDto(
//                        alarmMailActionDetailEntity.id,
//                        alarmMailActionDetailEntity.alarmActionId,
//                        alarmActionEntity.alarmActionName,
//                        alarmMailActionDetailEntity.alarmActionUserGroupId,
//                        alarmActionUserGroupEntity.userGroupName,
//                        alarmMailActionDetailEntity.subject,
//                        alarmMailActionDetailEntity.contents,
//                        alarmMailActionDetailEntity.eventName,
//                        alarmMailActionDetailEntity.eventTime,
//                        alarmMailActionDetailEntity.eventUser,
//                        alarmMailActionDetailEntity.eventComment
//                ))
//                .from(alarmMailActionDetailEntity)
//                .leftJoin(alarmActionEntity).on(alarmMailActionDetailEntity.alarmActionId.eq(alarmActionEntity.id))
//                .leftJoin(alarmActionUserGroupEntity).on(alarmMailActionDetailEntity.alarmActionUserGroupId.eq(alarmActionUserGroupEntity.id))
//                .where(
//                        alarmActionIdEq(condition.getAlarmActionId())
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
//        List<AlarmActionDetailResponseDto> content = query.fetch();
//
//        // 5. 카운트 조회 (isPaged()로 분기)
//        long total;
//        if (pageable.isPaged()) {
//            // [페이징 O] 별도 카운트 쿼리 실행
//            // [수정] .select(alarmActionEntity.count()) -> .select(alarmMailActionDetailEntity.count())
//            Long count = queryFactory
//                    .select(alarmMailActionDetailEntity.count())
//                    .from(alarmMailActionDetailEntity)
//                    .leftJoin(alarmActionEntity).on(alarmMailActionDetailEntity.alarmActionId.eq(alarmActionEntity.id))
//                    .leftJoin(alarmActionUserGroupEntity).on(alarmMailActionDetailEntity.alarmActionUserGroupId.eq(alarmActionUserGroupEntity.id))
//                    .where(
//                            alarmActionIdEq(condition.getAlarmActionId())
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
//                PathBuilder pathBuilder = new PathBuilder<>(alarmMailActionDetailEntity.getType(), alarmMailActionDetailEntity.getMetadata());
//
//                orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
//            }
//        }
//
//        // 기본 정렬 조건 (만약 정렬 조건이 없다면 id 내림차순)
//        if (orders.isEmpty()) {
//            orders.add(new OrderSpecifier(Order.DESC, alarmMailActionDetailEntity.id));
//        }
//
//        return orders.toArray(new OrderSpecifier[0]);
//    }
//
//    // == 동적 쿼리를 위한 BooleanExpression 메소드들 ==
//    private BooleanExpression alarmActionIdEq(Long alarmActionId) {
//        return alarmActionId!=null ? alarmMailActionDetailEntity.alarmActionId.eq(alarmActionId) : null;
//    }

}
