package kr.co.aim.infra.persistence.adapter;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.common.dto.AlarmActionUserGroupUsersResponseDto;
import kr.co.aim.common.dto.AlarmActionUserGroupUsersSearchConditionDto;
import kr.co.aim.common.dto.QAlarmActionUserGroupUsersResponseDto;
import kr.co.aim.domain.model.AlarmActionUserGroupUsers;
import kr.co.aim.domain.repository.AlarmActionUserGroupUsersRepository;
import kr.co.aim.infra.persistence.entity.AlarmActionUserGroupUsersEntity;
import kr.co.aim.infra.persistence.mapper.AlarmActionUserGroupUsersMapper;
import kr.co.aim.infra.persistence.springdatajpa.AlarmActionUserGroupUsersJpaRepository;
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

import static kr.co.aim.infra.persistence.entity.QAlarmActionUserGroupUsersEntity.alarmActionUserGroupUsersEntity;
import static kr.co.aim.infra.persistence.entity.QAlarmActionUserGroupEntity.alarmActionUserGroupEntity;

/**
 * UserRepository의 JPA 기반 구현체.
 * 실제 DB 작업은 Spring Data JPA가 제공하는 JpaRepository에 위임합니다.
 */

@Repository
@RequiredArgsConstructor
public class AlarmActionUserGroupUsersRepositoryImpl implements AlarmActionUserGroupUsersRepository {
    // Spring Data JPA가 자동으로 구현해주는 JPA 리포지토리. UserEntity를 다룬다.
    private final AlarmActionUserGroupUsersJpaRepository alarmActionUserGroupUsersJpaRepository;
    private final AlarmActionUserGroupUsersMapper alarmActionUserGroupUsersMapper;
    private final JPAQueryFactory queryFactory; // ✨ JPAQueryFactory 주입

    @Override
    public List<AlarmActionUserGroupUsers> findAll() {
        List<AlarmActionUserGroupUsersEntity> alarmActionUserGroupEntities = alarmActionUserGroupUsersJpaRepository.findAll();
        return alarmActionUserGroupEntities.stream().map(alarmActionUserGroupUsersMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<AlarmActionUserGroupUsers> findById(Long id) {
        Optional<AlarmActionUserGroupUsersEntity> optionalAlarmActionUserGroupEntity = alarmActionUserGroupUsersJpaRepository.findById(id);
        return optionalAlarmActionUserGroupEntity.map(alarmActionUserGroupUsersMapper::toDomain);
    }

    @Override
    public AlarmActionUserGroupUsers save(AlarmActionUserGroupUsers alarmActionUserGroup) {
        AlarmActionUserGroupUsersEntity entity = alarmActionUserGroupUsersMapper.toEntity(alarmActionUserGroup);
        AlarmActionUserGroupUsersEntity savedEntity = alarmActionUserGroupUsersJpaRepository.save(entity);
        return alarmActionUserGroupUsersMapper.toDomain(savedEntity);
    }

    @Override
    public List<AlarmActionUserGroupUsers> findByAlarmActionUserGroupId(Long alarmActionUserGroupId) {
        return alarmActionUserGroupUsersJpaRepository
                .findByAlarmActionUserGroupId(alarmActionUserGroupId)
                .stream().map(alarmActionUserGroupUsersMapper::toDomain)
                .collect(Collectors.toList());
    }


    @Override
    public void deleteAllByIdInBatch(List<Long> ids) {
        alarmActionUserGroupUsersJpaRepository.deleteAllByIdInBatch(ids);
    }

    @Override
    public Page<AlarmActionUserGroupUsersResponseDto> findAlarmActionUserGroupUsersWithConditions(AlarmActionUserGroupUsersSearchConditionDto condition, Pageable pageable) {

        // 1. 공통 쿼리 빌더 생성 (SELECT, FROM, JOIN, WHERE)
        JPAQuery<AlarmActionUserGroupUsersResponseDto> query = queryFactory
                .select(new QAlarmActionUserGroupUsersResponseDto(
                        alarmActionUserGroupUsersEntity.id,
                        alarmActionUserGroupUsersEntity.alarmActionUserGroupId,
                        alarmActionUserGroupEntity.userGroupName,
                        alarmActionUserGroupUsersEntity.userId,
                        alarmActionUserGroupUsersEntity.eventName,
                        alarmActionUserGroupUsersEntity.eventTime,
                        alarmActionUserGroupUsersEntity.eventUser,
                        alarmActionUserGroupUsersEntity.eventComment
                ))
                .from(alarmActionUserGroupUsersEntity)
                .leftJoin(alarmActionUserGroupEntity).on(alarmActionUserGroupUsersEntity.alarmActionUserGroupId.eq(alarmActionUserGroupEntity.id))
                .where(
                        // (WHERE 조건이 있다면 여기에 추가)
                        userGroupNameContains(condition.getUserGroupName())
                );

        // 2. 정렬 적용
        query.orderBy(getOrderSpecifiers(pageable.getSort()));

        // 3. 페이징 적용 (isPaged()로 분기)
        if (pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        // 4. 데이터 조회
        List<AlarmActionUserGroupUsersResponseDto> content = query.fetch();

        // 5. 카운트 조회 (isPaged()로 분기)
        long total;
        if (pageable.isPaged()) {
            // [페이징 O] 별도 카운트 쿼리 실행
            Long count = queryFactory
                    .select(alarmActionUserGroupUsersEntity.count())
                    .from(alarmActionUserGroupUsersEntity)
                    .leftJoin(alarmActionUserGroupEntity).on(alarmActionUserGroupUsersEntity.alarmActionUserGroupId.eq(alarmActionUserGroupEntity.id))
                    .where(
                            // (WHERE 조건이 있다면 여기에 추가)
                            userGroupNameContains(condition.getUserGroupName())
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
                PathBuilder pathBuilder = new PathBuilder<>(alarmActionUserGroupUsersEntity.getType(), alarmActionUserGroupUsersEntity.getMetadata());

                orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
            }
        }

        // 기본 정렬 조건 (만약 정렬 조건이 없다면 id 내림차순)
        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(Order.DESC, alarmActionUserGroupUsersEntity.id));
        }

        return orders.toArray(new OrderSpecifier[0]);
    }

    // == 동적 쿼리를 위한 BooleanExpression 메소드들 ==
    private BooleanExpression userGroupNameContains(String userGroupName) {
        return StringUtils.hasText(userGroupName) ? alarmActionUserGroupEntity.userGroupName.contains(userGroupName) : null;
    }


}
