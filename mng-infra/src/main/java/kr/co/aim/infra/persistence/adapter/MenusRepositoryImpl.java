package kr.co.aim.infra.persistence.adapter;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.common.dto.MenusResponseDto;
import kr.co.aim.common.dto.MenusSearchConditionDto;
import kr.co.aim.common.dto.QMenusResponseDto;
import kr.co.aim.domain.model.Menus;
import kr.co.aim.domain.repository.MenusRepository;
import kr.co.aim.infra.persistence.entity.MenusEntity;
import kr.co.aim.infra.persistence.entity.QMenusEntity;
import kr.co.aim.infra.persistence.mapper.MenusMapper;
import kr.co.aim.infra.persistence.springdatajpa.MenusJpaRepository;
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

import static kr.co.aim.infra.persistence.entity.QMenusEntity.menusEntity;
import static kr.co.aim.infra.persistence.entity.QSystemDefEntity.systemDefEntity;

/**
 * UserRepository의 JPA 기반 구현체.
 * 실제 DB 작업은 Spring Data JPA가 제공하는 JpaRepository에 위임합니다.
 */

@Repository
@RequiredArgsConstructor
public class MenusRepositoryImpl implements MenusRepository {

    private final MenusJpaRepository menusJpaRepository;
    private final MenusMapper menusMapper;
    private final JPAQueryFactory queryFactory; // ✨ JPAQueryFactory 주입

    @Override
    public List<Menus> findAll() {
        return menusJpaRepository.findAll().stream().map(menusMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<Menus> findById(Long id) {
        return menusJpaRepository.findById(id).map(menusMapper::toDomain);
    }

    @Override
    public Menus save(Menus menus) {
        // 1. Domain -> Entity 변환
        MenusEntity entity = menusMapper.toEntity(menus);
        // 2. JPA 리포지토리를 통해 DB에 저장
        MenusEntity savedEntity = menusJpaRepository.save(entity);
        // 3. 저장된 Entity -> Domain 변환 후 반환
        return menusMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteAllByIdInBatch(List<Long> ids) {
        menusJpaRepository.deleteAllByIdInBatch(ids);
    }

    @Override
    public Page<MenusResponseDto> findMenusWithConditions(MenusSearchConditionDto condition, Pageable pageable) {
        QMenusEntity joinMenus = new QMenusEntity("joinMenus"); // 별칭 필수!

        // 1. 공통 쿼리 빌더 생성 (SELECT, FROM, JOIN, WHERE)
        JPAQuery<MenusResponseDto> query = queryFactory
                .select(
                        new QMenusResponseDto(
                                menusEntity.id,
                                menusEntity.systemDefId,
                                systemDefEntity.systemDefName,
                                menusEntity.menuName,
                                menusEntity.parentMenuId,
                                joinMenus.menuName,
                                menusEntity.viewURL,
                                menusEntity.menuSEQ,
                                menusEntity.description,
                                menusEntity.iconName,
                                menusEntity.menuType,
                                menusEntity.checkOutState,
                                menusEntity.checkOutTime,
                                menusEntity.checkOutUser,
                                menusEntity.dataState,
                                menusEntity.eventName,
                                menusEntity.eventTime,
                                menusEntity.eventUser,
                                menusEntity.eventComment
                        )
                )
                .from(menusEntity)
                .leftJoin(joinMenus).on(menusEntity.parentMenuId.eq(joinMenus.id))
                .leftJoin(systemDefEntity).on(menusEntity.systemDefId.eq(systemDefEntity.id))
                .where(
                        menuNameContain(condition.getMenuName()),
                        viewURLContain(condition.getViewURL()),
                        menuTypeEq(condition.getMenuType()),
                        systemDefIdEq(condition.getSystemDefId()),
                        menuIdEq(condition.getId())
                );

        // 2. 정렬 적용
        query.orderBy(getOrderSpecifiers(pageable.getSort()));

        // 3. 페이징 적용 (isPaged()로 분기)
        if (pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        // 4. 데이터 조회
        List<MenusResponseDto> content = query.fetch();

        // 5. 카운트 조회 (isPaged()로 분기)
        long total;
        if (pageable.isPaged()) {
            // [페이징 O] 별도 카운트 쿼리 실행
            Long count = queryFactory
                    .select(menusEntity.count())
                    .from(menusEntity)
                    .leftJoin(joinMenus).on(menusEntity.parentMenuId.eq(joinMenus.id))
                    .leftJoin(systemDefEntity).on(menusEntity.systemDefId.eq(systemDefEntity.id))
                    .where(
                            menuNameContain(condition.getMenuName()),
                            viewURLContain(condition.getViewURL()),
                            menuTypeEq(condition.getMenuType()),
                            systemDefIdEq(condition.getSystemDefId()),
                            menuIdEq(condition.getId())
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
                PathBuilder pathBuilder = new PathBuilder<>(menusEntity.getType(), menusEntity.getMetadata());

                orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
            }
        }

        // 기본 정렬 조건 (만약 정렬 조건이 없다면 id 내림차순)
        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(Order.DESC, menusEntity.id));
        }

        return orders.toArray(new OrderSpecifier[0]);
    }

    // == 동적 쿼리를 위한 BooleanExpression 메소드들 ==
    private BooleanExpression menuNameContain(String menuName) {
        return StringUtils.hasText(menuName) ? menusEntity.menuName.contains(menuName) : null;
    }

    // == 동적 쿼리를 위한 BooleanExpression 메소드들 ==
    private BooleanExpression viewURLContain(String viewURL) {
        return StringUtils.hasText(viewURL) ? menusEntity.viewURL.contains(viewURL) : null;
    }

    // == 동적 쿼리를 위한 BooleanExpression 메소드들 ==
    private BooleanExpression menuTypeEq(String menuType) {
        return StringUtils.hasText(menuType) ? menusEntity.menuType.eq(menuType) : null;
    }

    // == 동적 쿼리를 위한 BooleanExpression 메소드들 ==
    private BooleanExpression systemDefIdEq(Long systemDefId) {
        return systemDefId!=null ? menusEntity.systemDefId.eq(systemDefId) : null;
    }

    // == 동적 쿼리를 위한 BooleanExpression 메소드들 ==
    private BooleanExpression menuIdEq(Long menuId) {
        return menuId!=null ? menusEntity.id.eq(menuId) : null;
    }
}
