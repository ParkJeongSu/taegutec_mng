package kr.co.aim.infra.persistence.adapter;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.common.Utils.QueryDslUtils;
import kr.co.aim.common.condition.EquipmentSearchCondition;
import kr.co.aim.domain.model.Equipment;
import kr.co.aim.domain.model.EquipmentHistory;
import kr.co.aim.domain.model.ProductionOrder;
import kr.co.aim.domain.repository.EquipmentRepository;
import kr.co.aim.infra.persistence.entity.EquipmentEntity;
import kr.co.aim.infra.persistence.entity.EquipmentHistoryEntity;
import kr.co.aim.infra.persistence.entity.ProductionOrderEntity;
import kr.co.aim.infra.persistence.mapper.EquipmentHistoryMapper;
import kr.co.aim.infra.persistence.mapper.EquipmentMapper;
import kr.co.aim.infra.persistence.springdatajpa.EquipmentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static kr.co.aim.infra.persistence.entity.QEquipmentEntity.equipmentEntity;
import static kr.co.aim.infra.persistence.entity.QEquipmentHistoryEntity.equipmentHistoryEntity;

/**
 * UserRepository의 JPA 기반 구현체.
 * 실제 DB 작업은 Spring Data JPA가 제공하는 JpaRepository에 위임합니다.
 */

@Repository
@RequiredArgsConstructor
public class EquipmentRepositoryImpl implements EquipmentRepository {
    // Spring Data JPA가 자동으로 구현해주는 JPA 리포지토리. UserEntity를 다룬다.
    private final EquipmentJpaRepository equipmentJpaRepository;
    private final EquipmentMapper equipmentMapper;
    private final EquipmentHistoryMapper  equipmentHistoryMapper;
    private final JPAQueryFactory queryFactory; // ✨ JPAQueryFactory 주입

    @Override
    public List<Equipment> findAll() {
        return equipmentJpaRepository.findAll().stream().map(equipmentMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<Equipment> findById(Long id) {
        return equipmentJpaRepository.findById(id).map(equipmentMapper::toDomain);
    }

    @Override
    public Optional<Equipment> findByEquipmentName(String equipmentName) {
        return equipmentJpaRepository.findByEquipmentName(equipmentName).map(equipmentMapper::toDomain);
    }

    //@PublishHistoryEvent
    @Override
    public Equipment save(Equipment equipment) {
        EquipmentEntity entity = equipmentMapper.toEntity(equipment);
        EquipmentEntity savedEntity = equipmentJpaRepository.save(entity);
        return equipmentMapper.toDomain(savedEntity);
    }

    @Override
    public Page<Equipment> findEquipmentByCondition(EquipmentSearchCondition condition, Pageable pageable) {
        //1. 공통 쿼리 빌더 생성 (SELECT, FROM, JOIN, WHERE)
        JPAQuery<EquipmentEntity> query = queryFactory
                .selectFrom(equipmentEntity)
                .where(
                        // (WHERE 조건이 있다면 여기에 추가)
                        equipmentNameContains(condition.getEquipmentName())
                );

        // 2. 정렬 적용
        query.orderBy(getOrderSpecifiers(pageable.getSort()));

        // 3. 페이징 적용 (isPaged()로 분기)
        if (pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        // 4. 데이터 조회
        List<EquipmentEntity> content = query.fetch();

        List<Equipment> converted = content.stream().map(equipmentMapper::toDomain).collect(Collectors.toList());

        // 5. 카운트 조회 (isPaged()로 분기)
        long total;
        if (pageable.isPaged()) {
            // [페이징 O] 별도 카운트 쿼리 실행
            Long count = queryFactory
                    .select(equipmentEntity.count())
                    .from(equipmentEntity)
                    .where(
                            // (WHERE 조건이 있다면 여기에 추가)
                            equipmentNameContains(condition.getEquipmentName())
                    )
                    .fetchOne();

            total = (count != null) ? count.longValue() : 0L;

        } else {
            // [페이징 X] .unpaged() 일 때
            total = content.size();
        }

        // 6. PageImpl 반환
        return new PageImpl<>(converted, pageable, total);
    }

    private OrderSpecifier<?>[] getOrderSpecifiers(Sort sort) {
        List<OrderSpecifier> orders = new ArrayList<>();

        if (sort != null && sort.isSorted()) {
            for (Sort.Order order : sort) {
                String property = order.getProperty();

                // [핵심] property가 null이 아니고, 공백이 아닐 때만 정렬을 추가합니다.
                if (StringUtils.hasText(property) && QueryDslUtils.isValidProperty(property)) {
                    Order direction = order.isAscending() ? Order.ASC : Order.DESC;

                    PathBuilder pathBuilder = new PathBuilder<>(
                            equipmentEntity.getType(),
                            equipmentEntity.getMetadata()
                    );

                    orders.add(new OrderSpecifier(direction, pathBuilder.get(property)));
                }
            }
        }

        // 유효한 정렬 필드가 하나도 없었다면 (스웨거에서 잘못 보낸 경우 포함) 기본값 적용
        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(Order.DESC, equipmentEntity.id));
        }

        return orders.toArray(new OrderSpecifier[0]);
    }

    @Override
    public void deleteAllByIdInBatch(List<Long> ids) {
        equipmentJpaRepository.deleteAllByIdInBatch(ids);
    }

    @Override
    public List<EquipmentHistory> findEquipmentHistoryByPeriod(LocalDateTime start, LocalDateTime end) {
        // QEquipmentHistoryEntity 스태틱 임포트 가정
        List<EquipmentHistoryEntity> entities = queryFactory
                .selectFrom(equipmentHistoryEntity)
                .where(equipmentHistoryEntity.eventTime.between(start, end))
                .orderBy(equipmentHistoryEntity.equipmentName.asc(), equipmentHistoryEntity.eventTime.asc())
                .fetch();

        List<kr.co.aim.domain.model.EquipmentHistory> domains = new ArrayList<>();
        for (EquipmentHistoryEntity entity : entities) {
            domains.add(equipmentHistoryMapper.toDomain(entity)); // 해당 매퍼 주입 필요
        }
        return domains;
    }


    // == 동적 쿼리를 위한 BooleanExpression 메소드들 ==
    private BooleanExpression equipmentNameContains(String equipmentName) {
        return StringUtils.hasText(equipmentName) ? equipmentEntity.equipmentName.contains(equipmentName) : null;
    }
}
