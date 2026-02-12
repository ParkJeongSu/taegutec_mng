package kr.co.aim.infra.persistence.adapter;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.common.dto.AlarmDefResponseDto;
import kr.co.aim.common.dto.AlarmDefSearchConditionDto;
import kr.co.aim.common.dto.QAlarmDefResponseDto;
import kr.co.aim.domain.model.AlarmDef;
import kr.co.aim.domain.repository.AlarmDefRepository;
import kr.co.aim.infra.persistence.entity.AlarmDefEntity;
import kr.co.aim.infra.persistence.mapper.AlarmDefMapper;
import kr.co.aim.infra.persistence.springdatajpa.AlarmDefJpaRepository;
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

import static kr.co.aim.infra.persistence.entity.QAlarmDefEntity.alarmDefEntity;

/**
 * UserRepository의 JPA 기반 구현체.
 * 실제 DB 작업은 Spring Data JPA가 제공하는 JpaRepository에 위임합니다.
 */

@Repository
@RequiredArgsConstructor
public class AlarmDefRepositoryImpl implements AlarmDefRepository {
    // Spring Data JPA가 자동으로 구현해주는 JPA 리포지토리. UserEntity를 다룬다.
    private final AlarmDefJpaRepository alarmDefJpaRepository;
    private final AlarmDefMapper alarmDefMapper;
    private final JPAQueryFactory queryFactory; // ✨ JPAQueryFactory 주입

    @Override
    public List<AlarmDef> findAll() {
        List<AlarmDefEntity> alarmDefEntities = alarmDefJpaRepository.findAll();
        return alarmDefEntities.stream().map(alarmDefMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<AlarmDef> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public Optional<AlarmDef> findByAlarmDefName(String alarmCodeName) {
        Optional<AlarmDefEntity> alarmDefEntityOptional = alarmDefJpaRepository.findByAlarmDefName(alarmCodeName).stream().findFirst();
        return alarmDefEntityOptional.map(alarmDefMapper::toDomain);
    }

    @Override
    public AlarmDef save(AlarmDef alarmDef) {
        AlarmDefEntity entity = alarmDefMapper.toEntity(alarmDef);
        AlarmDefEntity savedEntity = alarmDefJpaRepository.save(entity);
        return alarmDefMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteAllByIdInBatch(List<Long> ids) {
        alarmDefJpaRepository.deleteAllByIdInBatch(ids);
    }

    @Override
    public Page<AlarmDefResponseDto> findAlarmDefWithConditions(AlarmDefSearchConditionDto condition, Pageable pageable) {

        // 1. 공통 쿼리 빌더 생성 (SELECT, FROM, WHERE)
        JPAQuery<AlarmDefResponseDto> query = queryFactory
                .select(new QAlarmDefResponseDto(
                        alarmDefEntity.id,
                        alarmDefEntity.alarmDefName,
                        alarmDefEntity.alarmType,
                        alarmDefEntity.description,
                        alarmDefEntity.alarmLevel,
                        alarmDefEntity.dataState,
                        alarmDefEntity.checkOutState,
                        alarmDefEntity.checkOutTime,
                        alarmDefEntity.checkOutUser,
                        alarmDefEntity.eventName,
                        alarmDefEntity.eventTime,
                        alarmDefEntity.eventUser,
                        alarmDefEntity.eventComment
                ))
                .from(alarmDefEntity)
                .where(
                        idEq(condition.getId()),
                        alarmTypeEq(condition.getAlarmType()),
                        alarmLevelEq(condition.getAlarmLevel()),
                        alarmDefNameContains(condition.getAlarmDefName())
                );

        // 2. 정렬 적용
        query.orderBy(getOrderSpecifiers(pageable.getSort()));

        // 3. 페이징 적용 (isPaged()로 분기)
        if (pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        // 4. 데이터 조회
        List<AlarmDefResponseDto> content = query.fetch();

        // 5. 카운트 조회 (isPaged()로 분기)
        long total;
        if (pageable.isPaged()) {
            // [페이징 O] 별도 카운트 쿼리 실행
            Long count = queryFactory
                    .select(alarmDefEntity.count())
                    .from(alarmDefEntity)
                    .where(
                            idEq(condition.getId()),
                            alarmTypeEq(condition.getAlarmType()),
                            alarmLevelEq(condition.getAlarmLevel()),
                            alarmDefNameContains(condition.getAlarmDefName())
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
                PathBuilder pathBuilder = new PathBuilder<>(alarmDefEntity.getType(), alarmDefEntity.getMetadata());

                orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
            }
        }

        // 기본 정렬 조건 (만약 정렬 조건이 없다면 id 내림차순)
        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(Order.DESC, alarmDefEntity.id));
        }

        return orders.toArray(new OrderSpecifier[0]);
    }

    // == 동적 쿼리를 위한 BooleanExpression 메소드들 ==

    private BooleanExpression idEq(Long id) {
        return id!=null ? alarmDefEntity.id.eq(id) : null;
    }

    private BooleanExpression alarmTypeEq(String alarmType) {
        return StringUtils.hasText(alarmType) ? alarmDefEntity.alarmType.eq(alarmType) : null;
    }

    private BooleanExpression alarmLevelEq(String alarmLevel) {
        return StringUtils.hasText(alarmLevel) ? alarmDefEntity.alarmLevel.eq(alarmLevel) : null;
    }

    private BooleanExpression alarmDefNameContains(String alarmDefName) {
        return StringUtils.hasText(alarmDefName) ? alarmDefEntity.alarmDefName.contains(alarmDefName) : null;
    }


}
