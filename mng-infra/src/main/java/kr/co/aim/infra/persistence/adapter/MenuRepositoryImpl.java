package kr.co.aim.infra.persistence.adapter;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.domain.model.Menu;
import kr.co.aim.domain.repository.MenuRepository;
import kr.co.aim.infra.persistence.entity.MenuEntity;
import kr.co.aim.infra.persistence.entity.QMenuEntity;
import kr.co.aim.infra.persistence.entity.QUserGroupEntity;
import kr.co.aim.infra.persistence.entity.QUserGroupMemberEntity;
import kr.co.aim.infra.persistence.entity.QUserGroupMenuAuthEntity;
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
    public List<Menu> findAuthorizedMenusByUserId(Long userId) {
        if (userId == null) {
            return new ArrayList<>();
        }

        List<MenuEntity> entities = queryFactory
                .select(menu)
                .distinct()
                .from(menu)
                .join(menuAuth).on(menuAuth.menuId.eq(menu.id))
                .join(userGroup).on(userGroup.id.eq(menuAuth.userGroupId))
                .join(groupMember).on(groupMember.userGroupId.eq(userGroup.id))
                .where(
                        groupMemberUserIdEq(userId),
                        userGroupUseStateEq("ACTIVE"),
                        menuUseStateEq("ACTIVE"),
                        menuIsVisibleEq("Y")
                )
                .orderBy(
                        menu.menuLevel.asc(),
                        menu.displayOrder.asc(),
                        menu.id.asc()
                )
                .fetch();

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

    // =========================================================================
    // == 동적 쿼리 및 조건절 분리를 위한 BooleanExpression 메서드 ==
    // =========================================================================

    private BooleanExpression groupMemberUserIdEq(Long userId) {
        return userId != null ? groupMember.userId.eq(userId) : null;
    }

    private BooleanExpression userGroupUseStateEq(String useState) {
        return StringUtils.hasText(useState) ? userGroup.useState.equalsIgnoreCase(useState) : null;
    }

    private BooleanExpression menuUseStateEq(String useState) {
        return StringUtils.hasText(useState) ? menu.useState.equalsIgnoreCase(useState) : null;
    }

    private BooleanExpression menuIsVisibleEq(String isVisible) {
        return StringUtils.hasText(isVisible) ? menu.isVisible.equalsIgnoreCase(isVisible) : null;
    }

    private BooleanExpression menuFactoryNameEq(String factoryName) {
        return StringUtils.hasText(factoryName) ? menu.factoryName.equalsIgnoreCase(factoryName) : null;
    }

    private BooleanExpression menuNameContains(String menuName) {
        return StringUtils.hasText(menuName) ? menu.menuName.contains(menuName) : null;
    }
}