package kr.co.aim.infra.persistence.adapter;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.domain.model.Menu;
import kr.co.aim.domain.repository.MenuRepository;
import kr.co.aim.infra.persistence.entity.*;
import kr.co.aim.infra.persistence.mapper.MenuMapper;
import kr.co.aim.infra.persistence.springdatajpa.MenuJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * MenuRepository의 JPA 기반 어댑터 구현체.
 * Spring Data JPA와 QueryDSL을 활용하며, 동적 조건 처리를 위해 BooleanExpression 메서드를 분리하여 사용합니다.
 */
@Repository
@RequiredArgsConstructor
public class MenuRepositoryImpl implements MenuRepository {

    private final MenuJpaRepository menuJpaRepository;
    private final MenuMapper menuMapper;
    private final JPAQueryFactory queryFactory;

    private static final QMenuEntity menu = QMenuEntity.menuEntity;
    private static final QUserGroupMenuAuthEntity menuAuth = QUserGroupMenuAuthEntity.userGroupMenuAuthEntity;
    private static final QUserGroupMemberEntity groupMember = QUserGroupMemberEntity.userGroupMemberEntity;
    private static final QUserGroupEntity userGroup = QUserGroupEntity.userGroupEntity;
    private static final QSysUserEntity sysUser = QSysUserEntity.sysUserEntity;

    @Override
    public List<Menu> findAll() {
        List<MenuEntity> entities = menuJpaRepository.findAll();
        List<Menu> result = new ArrayList<>();
        for (MenuEntity entity : entities) {
            if (entity != null) {
                result.add(menuMapper.toDomain(entity));
            }
        }
        return result;
    }

    @Override
    public Optional<Menu> findById(Long id) {
        Optional<MenuEntity> entityOptional = menuJpaRepository.findById(id);
        if (entityOptional.isPresent()) {
            return Optional.ofNullable(menuMapper.toDomain(entityOptional.get()));
        }
        return Optional.empty();
    }

    @Override
    public List<Menu> findByFactoryNameOrderByMenuLevelAscDisplayOrderAsc(String factoryName) {
        List<MenuEntity> entities = menuJpaRepository.findByFactoryNameOrderByMenuLevelAscDisplayOrderAsc(factoryName);
        List<Menu> result = new ArrayList<>();
        for (MenuEntity entity : entities) {
            if (entity != null) {
                result.add(menuMapper.toDomain(entity));
            }
        }
        return result;
    }

    @Override
    public Optional<Menu> findByFactoryNameAndMenuName(String factoryName, String menuName) {
        Optional<MenuEntity> entityOptional = menuJpaRepository.findByFactoryNameAndMenuName(factoryName, menuName);
        if (entityOptional.isPresent()) {
            return Optional.ofNullable(menuMapper.toDomain(entityOptional.get()));
        }
        return Optional.empty();
    }

    @Override
    public Menu save(Menu menu) {
        MenuEntity entity = menuMapper.toEntity(menu);
        MenuEntity savedEntity = menuJpaRepository.save(entity);
        return menuMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(Long id) {
        menuJpaRepository.deleteById(id);
    }

    @Override
    public void deleteAllByIdInBatch(List<Long> ids) {
        menuJpaRepository.deleteAllByIdInBatch(ids);
    }

    @Override
    public List<Menu> findAuthorizedMenusByUserId(String userId) {
        if (!StringUtils.hasText(userId)) {
            return new ArrayList<>();
        }

        // 1. 해당 사용자가 ADMIN 권한 그룹에 속해 있는지 확인
        boolean isAdmin = checkUserHasAdminGroup(userId);

        List<MenuEntity> entities;

        if (isAdmin) {
            // ADMIN 그룹 사용자인 경우: 별도의 권한 테이블 체크 없이 전체 활성 메뉴 조회
            entities = queryFactory
                    .selectFrom(menu)
                    .where(
                            menuUseStateEq("ACTIVE"),
                            menuIsVisibleEq("Y")
                    )
                    .orderBy(
                            menu.menuLevel.asc(),
                            menu.displayOrder.asc(),
                            menu.id.asc()
                    )
                    .fetch();
        } else {
            // 일반 사용자 그룹인 경우: USER_GROUP_MENU_AUTH 매핑 기반 조회
            entities = queryFactory
                    .select(menu)
                    .distinct()
                    .from(menu)
                    .join(menuAuth).on(menuAuth.menuId.eq(menu.id))
                    .join(userGroup).on(userGroup.id.eq(menuAuth.userGroupId))
                    .join(groupMember).on(groupMember.userGroupId.eq(userGroup.id))
                    .join(sysUser).on(sysUser.id.eq(groupMember.userId)) // 올바른 FK 조인: SYS_USER.ID = USER_GROUP_MEMBER.USER_ID
                    .where(
                            sysUserUserIdEq(userId),
                            userGroupUseStateEq("USE"),
                            menuUseStateEq("ACTIVE"),
                            menuIsVisibleEq("Y")
                    )
                    .orderBy(
                            menu.menuLevel.asc(),
                            menu.displayOrder.asc(),
                            menu.id.asc()
                    )
                    .fetch();
        }

        List<Menu> result = new ArrayList<>();
        if (entities != null) {
            for (MenuEntity entity : entities) {
                if (entity != null) {
                    result.add(menuMapper.toDomain(entity));
                }
            }
        }
        return result;
    }

    /**
     * 사용자가 대소문자 무관 'ADMIN'이 포함된 활성 그룹에 속해 있는지 확인
     */
    private boolean checkUserHasAdminGroup(String userId) {
        Integer fetchOne = queryFactory
                .selectOne()
                .from(groupMember)
                .join(userGroup).on(userGroup.id.eq(groupMember.userGroupId))
                .join(sysUser).on(sysUser.id.eq(groupMember.userId))
                .where(
                        sysUserUserIdEq(userId),
                        userGroupUseStateEq("USE"),
                        userGroupNameContainsIgnoreCase("ADMIN")
                )
                .fetchFirst();

        return fetchOne != null;
    }

    // =========================================================================
    // == 동적 쿼리 및 조건절 분리를 위한 BooleanExpression 메서드 ==
    // =========================================================================

    private BooleanExpression groupMemberUserIdEq(Long userId) {
        return userId != null ? groupMember.userId.eq(userId) : null;
    }

    private BooleanExpression userGroupUseStateEq(String useState) {
        return StringUtils.hasText(useState) ? userGroup.useState.equalsIgnoreCase(useState) : null;
    }

    private BooleanExpression userGroupNameContainsIgnoreCase(String groupName) {
        return StringUtils.hasText(groupName) ? userGroup.userGroupName.containsIgnoreCase(groupName) : null;
    }

    private BooleanExpression menuUseStateEq(String useState) {
        return StringUtils.hasText(useState) ? menu.useState.equalsIgnoreCase(useState) : null;
    }

    private BooleanExpression menuIsVisibleEq(String isVisible) {
        return StringUtils.hasText(isVisible) ? menu.isVisible.equalsIgnoreCase(isVisible) : null;
    }

    private BooleanExpression sysUserUserIdEq(String userId) {
        return StringUtils.hasText(userId) ? sysUser.userId.eq(userId) : null;
    }

    private BooleanExpression menuFactoryNameEq(String factoryName) {
        return StringUtils.hasText(factoryName) ? menu.factoryName.equalsIgnoreCase(factoryName) : null;
    }

    private BooleanExpression menuNameContains(String menuName) {
        return StringUtils.hasText(menuName) ? menu.menuName.contains(menuName) : null;
    }
}