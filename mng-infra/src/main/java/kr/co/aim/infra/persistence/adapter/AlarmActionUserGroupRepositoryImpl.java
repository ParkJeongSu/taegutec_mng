package kr.co.aim.infra.persistence.adapter;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.domain.model.AlarmActionUserGroup;
import kr.co.aim.domain.repository.AlarmActionUserGroupRepository;
import kr.co.aim.infra.persistence.entity.AlarmActionUserGroupEntity;
import kr.co.aim.infra.persistence.mapper.AlarmActionUserGroupMapper;
import kr.co.aim.infra.persistence.springdatajpa.AlarmActionUserGroupJpaRepository;
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
public class AlarmActionUserGroupRepositoryImpl implements AlarmActionUserGroupRepository {
    // Spring Data JPA가 자동으로 구현해주는 JPA 리포지토리. UserEntity를 다룬다.
    private final AlarmActionUserGroupJpaRepository alarmActionUserGroupJpaRepository;
    private final AlarmActionUserGroupMapper alarmActionUserGroupMapper;
    private final JPAQueryFactory queryFactory; // ✨ JPAQueryFactory 주입

    @Override
    public List<AlarmActionUserGroup> findAll() {
        List<AlarmActionUserGroupEntity> alarmActionUserGroupEntities = alarmActionUserGroupJpaRepository.findAll();
        return alarmActionUserGroupEntities.stream().map(alarmActionUserGroupMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<AlarmActionUserGroup> findById(Long id) {
        Optional<AlarmActionUserGroupEntity> optionalAlarmActionUserGroupEntity = alarmActionUserGroupJpaRepository.findById(id);
        return optionalAlarmActionUserGroupEntity.map(alarmActionUserGroupMapper::toDomain);
    }

    @Override
    public AlarmActionUserGroup save(AlarmActionUserGroup alarmActionUserGroup) {
        AlarmActionUserGroupEntity entity = alarmActionUserGroupMapper.toEntity(alarmActionUserGroup);
        AlarmActionUserGroupEntity savedEntity = alarmActionUserGroupJpaRepository.save(entity);
        return alarmActionUserGroupMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<AlarmActionUserGroup> findByUserGroupName(String userGroupName) {
        Optional<AlarmActionUserGroupEntity> entity = alarmActionUserGroupJpaRepository.findByUserGroupName(userGroupName);
        return entity.map(alarmActionUserGroupMapper::toDomain);
    }

    @Override
    public void deleteAllByIdInBatch(List<Long> ids) {
        alarmActionUserGroupJpaRepository.deleteAllByIdInBatch(ids);
    }

//    @Override
//    public Page<AlarmActionUserGroupResponseDto> findAlarmUserGroupWithConditions(AlarmActionUserGroupSearchConditionDto condition, Pageable pageable) {
//
//        // 1. 공통 쿼리 빌더 생성 (SELECT, FROM, WHERE)
//        JPAQuery<AlarmActionUserGroupResponseDto> query = queryFactory
//                .select(new QAlarmActionUserGroupResponseDto(
//                        alarmActionUserGroupEntity.id,
//                        alarmActionUserGroupEntity.userGroupName,
//                        alarmActionUserGroupEntity.eventName,
//                        alarmActionUserGroupEntity.eventTime,
//                        alarmActionUserGroupEntity.eventUser,
//                        alarmActionUserGroupEntity.eventComment
//                ))
//                .from(alarmActionUserGroupEntity)
//                .where(
//                        // (WHERE 조건이 있다면 여기에 추가)
//                        userGroupNameContains(condition.getUserGroupName())
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
//        List<AlarmActionUserGroupResponseDto> content = query.fetch();
//
//        // 5. 카운트 조회 (isPaged()로 분기)
//        long total;
//        if (pageable.isPaged()) {
//            // [페이징 O] 별도 카운트 쿼리 실행
//            Long count = queryFactory
//                    .select(alarmActionUserGroupEntity.count())
//                    .from(alarmActionUserGroupEntity)
//                    .where(
//                            // (WHERE 조건이 있다면 여기에 추가)
//                            userGroupNameContains(condition.getUserGroupName())
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
//                PathBuilder pathBuilder = new PathBuilder<>(alarmActionUserGroupEntity.getType(), alarmActionUserGroupEntity.getMetadata());
//
//                orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
//            }
//        }
//
//        // 기본 정렬 조건 (만약 정렬 조건이 없다면 id 내림차순)
//        if (orders.isEmpty()) {
//            orders.add(new OrderSpecifier(Order.DESC, alarmActionUserGroupEntity.id));
//        }
//
//        return orders.toArray(new OrderSpecifier[0]);
//    }
//
//    // == 동적 쿼리를 위한 BooleanExpression 메소드들 ==
//    private BooleanExpression userGroupNameContains(String userGroupName) {
//        return StringUtils.hasText(userGroupName) ? alarmActionUserGroupEntity.userGroupName.contains(userGroupName) : null;
//    }

}
