package kr.co.aim.infra.persistence.adapter;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.common.condition.UserGroupMenuAuthSearchCondition;
import kr.co.aim.domain.model.UserGroupMenuAuth;
import kr.co.aim.domain.repository.UserGroupMenuAuthRepository;
import kr.co.aim.infra.persistence.entity.UserGroupMenuAuthEntity;
import kr.co.aim.infra.persistence.mapper.UserGroupMenuAuthMapper;
import kr.co.aim.infra.persistence.springdatajpa.UserGroupMenuAuthJpaRepository;
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

import static kr.co.aim.infra.persistence.entity.QMenuEntity.menuEntity;
import static kr.co.aim.infra.persistence.entity.QUserGroupEntity.userGroupEntity;
import static kr.co.aim.infra.persistence.entity.QUserGroupMenuAuthEntity.userGroupMenuAuthEntity;

/**
 * UserGroupMenuAuthRepository의 JPA 및 QueryDSL 기반 어댑터 구현체.
 * 3-Way JOIN (USER_GROUP_MENU_AUTH + USER_GROUP + MENU) 프로젝션을 제공합니다.
 */
@Repository
@RequiredArgsConstructor
public class UserGroupMenuAuthRepositoryImpl implements UserGroupMenuAuthRepository {

    private final UserGroupMenuAuthJpaRepository userGroupMenuAuthJpaRepository;
    private final UserGroupMenuAuthMapper userGroupMenuAuthMapper;
    private final JPAQueryFactory queryFactory;

    @Override
    public List<UserGroupMenuAuth> findAll() {
        List<UserGroupMenuAuthEntity> entities = userGroupMenuAuthJpaRepository.findAll();
        List<UserGroupMenuAuth> result = new ArrayList<>();
        for (UserGroupMenuAuthEntity entity : entities) {
            result.add(userGroupMenuAuthMapper.toDomain(entity));
        }
        return result;
    }

    @Override
    public Optional<UserGroupMenuAuth> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }

        Tuple tuple = queryFactory
                .select(
                        userGroupMenuAuthEntity,
                        userGroupEntity.userGroupName,
                        menuEntity.menuName,
                        menuEntity.parentId,
                        menuEntity.menuLevel,
                        menuEntity.displayOrder,
                        menuEntity.filePath,
                        menuEntity.routerPath
                )
                .from(userGroupMenuAuthEntity)
                .leftJoin(userGroupEntity).on(userGroupMenuAuthEntity.userGroupId.eq(userGroupEntity.id))
                .leftJoin(menuEntity).on(userGroupMenuAuthEntity.menuId.eq(menuEntity.id))
                .where(userGroupMenuAuthEntity.id.eq(id))
                .fetchOne();

        if (tuple == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(mapTupleToDomain(tuple));
    }

    @Override
    public List<UserGroupMenuAuth> findByUserGroupId(Long userGroupId) {
        if (userGroupId == null) {
            return new ArrayList<>();
        }

        List<Tuple> tuples = queryFactory
                .select(
                        userGroupMenuAuthEntity,
                        userGroupEntity.userGroupName,
                        menuEntity.menuName,
                        menuEntity.parentId,
                        menuEntity.menuLevel,
                        menuEntity.displayOrder,
                        menuEntity.filePath,
                        menuEntity.routerPath
                )
                .from(userGroupMenuAuthEntity)
                .leftJoin(userGroupEntity).on(userGroupMenuAuthEntity.userGroupId.eq(userGroupEntity.id))
                .leftJoin(menuEntity).on(userGroupMenuAuthEntity.menuId.eq(menuEntity.id))
                .where(userGroupMenuAuthEntity.userGroupId.eq(userGroupId))
                .orderBy(menuEntity.menuLevel.asc().nullsLast(), menuEntity.displayOrder.asc().nullsLast(), userGroupMenuAuthEntity.id.desc())
                .fetch();

        List<UserGroupMenuAuth> result = new ArrayList<>();
        for (Tuple tuple : tuples) {
            UserGroupMenuAuth domain = mapTupleToDomain(tuple);
            if (domain != null) {
                result.add(domain);
            }
        }
        return result;
    }

    @Override
    public Optional<UserGroupMenuAuth> findByUserGroupIdAndMenuId(Long userGroupId, Long menuId) {
        Optional<UserGroupMenuAuthEntity> entityOptional = userGroupMenuAuthJpaRepository.findByUserGroupIdAndMenuId(userGroupId, menuId);
        if (entityOptional.isPresent()) {
            return Optional.ofNullable(userGroupMenuAuthMapper.toDomain(entityOptional.get()));
        }
        return Optional.empty();
    }

    @Override
    public UserGroupMenuAuth save(UserGroupMenuAuth userGroupMenuAuth) {
        UserGroupMenuAuthEntity entity = userGroupMenuAuthMapper.toEntity(userGroupMenuAuth);
        UserGroupMenuAuthEntity savedEntity = userGroupMenuAuthJpaRepository.save(entity);
        UserGroupMenuAuth domain = userGroupMenuAuthMapper.toDomain(savedEntity);
        domain.setUserGroupName(userGroupMenuAuth.getUserGroupName());
        domain.setMenuName(userGroupMenuAuth.getMenuName());
        domain.setParentId(userGroupMenuAuth.getParentId());
        domain.setMenuLevel(userGroupMenuAuth.getMenuLevel());
        domain.setDisplayOrder(userGroupMenuAuth.getDisplayOrder());
        domain.setFilePath(userGroupMenuAuth.getFilePath());
        domain.setRouterPath(userGroupMenuAuth.getRouterPath());
        return domain;
    }

    @Override
    public void deleteById(Long id) {
        userGroupMenuAuthJpaRepository.deleteById(id);
    }

    @Override
    public void deleteAllByIdInBatch(List<Long> ids) {
        userGroupMenuAuthJpaRepository.deleteAllByIdInBatch(ids);
    }

    @Override
    public Page<UserGroupMenuAuth> findUserGroupMenuAuthsWithDetails(UserGroupMenuAuthSearchCondition condition, Pageable pageable) {
        String factoryName = condition != null ? condition.getFactoryName() : null;
        Long userGroupId = condition != null ? condition.getUserGroupId() : null;
        String userGroupName = condition != null ? condition.getUserGroupName() : null;
        Long menuId = condition != null ? condition.getMenuId() : null;
        String menuName = condition != null ? condition.getMenuName() : null;
        String authSelect = condition != null ? condition.getAuthSelect() : null;
        String authSave = condition != null ? condition.getAuthSave() : null;
        String authDelete = condition != null ? condition.getAuthDelete() : null;

        JPAQuery<Tuple> query = queryFactory
                .select(
                        userGroupMenuAuthEntity,
                        userGroupEntity.userGroupName,
                        menuEntity.menuName,
                        menuEntity.parentId,
                        menuEntity.menuLevel,
                        menuEntity.displayOrder,
                        menuEntity.filePath,
                        menuEntity.routerPath
                )
                .from(userGroupMenuAuthEntity)
                .leftJoin(userGroupEntity).on(userGroupMenuAuthEntity.userGroupId.eq(userGroupEntity.id))
                .leftJoin(menuEntity).on(userGroupMenuAuthEntity.menuId.eq(menuEntity.id))
                .where(
                        factoryNameContains(factoryName),
                        userGroupIdEq(userGroupId),
                        userGroupNameContains(userGroupName),
                        menuIdEq(menuId),
                        menuNameContains(menuName),
                        authSelectEq(authSelect),
                        authSaveEq(authSave),
                        authDeleteEq(authDelete)
                );

        query.orderBy(getOrderSpecifiers(pageable != null ? pageable.getSort() : Sort.unsorted()));

        if (pageable != null && pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        List<Tuple> content = query.fetch();
        List<UserGroupMenuAuth> converted = new ArrayList<>();
        for (Tuple tuple : content) {
            UserGroupMenuAuth domain = mapTupleToDomain(tuple);
            if (domain != null) {
                converted.add(domain);
            }
        }

        long total;
        if (pageable != null && pageable.isPaged()) {
            Long count = queryFactory
                    .select(userGroupMenuAuthEntity.count())
                    .from(userGroupMenuAuthEntity)
                    .leftJoin(userGroupEntity).on(userGroupMenuAuthEntity.userGroupId.eq(userGroupEntity.id))
                    .leftJoin(menuEntity).on(userGroupMenuAuthEntity.menuId.eq(menuEntity.id))
                    .where(
                            factoryNameContains(factoryName),
                            userGroupIdEq(userGroupId),
                            userGroupNameContains(userGroupName),
                            menuIdEq(menuId),
                            menuNameContains(menuName),
                            authSelectEq(authSelect),
                            authSaveEq(authSave),
                            authDeleteEq(authDelete)
                    )
                    .fetchOne();

            total = (count != null) ? count.longValue() : 0L;
        } else {
            total = converted.size();
        }

        return new PageImpl<>(converted, pageable != null ? pageable : Pageable.unpaged(), total);
    }

    private UserGroupMenuAuth mapTupleToDomain(Tuple tuple) {
        if (tuple == null) {
            return null;
        }
        UserGroupMenuAuthEntity entity = tuple.get(userGroupMenuAuthEntity);
        if (entity == null) {
            return null;
        }
        UserGroupMenuAuth domain = userGroupMenuAuthMapper.toDomain(entity);
        domain.setUserGroupName(tuple.get(userGroupEntity.userGroupName));
        domain.setMenuName(tuple.get(menuEntity.menuName));
        domain.setParentId(tuple.get(menuEntity.parentId));
        domain.setMenuLevel(tuple.get(menuEntity.menuLevel));
        domain.setDisplayOrder(tuple.get(menuEntity.displayOrder));
        domain.setFilePath(tuple.get(menuEntity.filePath));
        domain.setRouterPath(tuple.get(menuEntity.routerPath));
        return domain;
    }

    private OrderSpecifier<?>[] getOrderSpecifiers(Sort sort) {
        List<OrderSpecifier> orders = new ArrayList<>();

        if (sort != null && sort.isSorted()) {
            for (Sort.Order order : sort) {
                Order direction = order.isAscending() ? Order.ASC : Order.DESC;
                PathBuilder pathBuilder = new PathBuilder<>(userGroupMenuAuthEntity.getType(), userGroupMenuAuthEntity.getMetadata());
                orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
            }
        }

        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(Order.DESC, userGroupMenuAuthEntity.id));
        }

        return orders.toArray(new OrderSpecifier[0]);
    }

    private BooleanExpression factoryNameContains(String factoryName) {
        return StringUtils.hasText(factoryName) ? userGroupMenuAuthEntity.factoryName.contains(factoryName) : null;
    }

    private BooleanExpression userGroupIdEq(Long userGroupId) {
        return userGroupId != null ? userGroupMenuAuthEntity.userGroupId.eq(userGroupId) : null;
    }

    private BooleanExpression userGroupNameContains(String userGroupName) {
        return StringUtils.hasText(userGroupName) ? userGroupEntity.userGroupName.contains(userGroupName) : null;
    }

    private BooleanExpression menuIdEq(Long menuId) {
        return menuId != null ? userGroupMenuAuthEntity.menuId.eq(menuId) : null;
    }

    private BooleanExpression menuNameContains(String menuName) {
        return StringUtils.hasText(menuName) ? menuEntity.menuName.contains(menuName) : null;
    }

    private BooleanExpression authSelectEq(String authSelect) {
        return StringUtils.hasText(authSelect) ? userGroupMenuAuthEntity.authSelect.equalsIgnoreCase(authSelect) : null;
    }

    private BooleanExpression authSaveEq(String authSave) {
        return StringUtils.hasText(authSave) ? userGroupMenuAuthEntity.authSave.equalsIgnoreCase(authSave) : null;
    }

    private BooleanExpression authDeleteEq(String authDelete) {
        return StringUtils.hasText(authDelete) ? userGroupMenuAuthEntity.authDelete.equalsIgnoreCase(authDelete) : null;
    }
}
