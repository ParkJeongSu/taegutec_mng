package kr.co.aim.infra.persistence.adapter;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.common.condition.SysUserSearchCondition;
import kr.co.aim.domain.model.SysUser;
import kr.co.aim.domain.repository.UserRepository;
import kr.co.aim.infra.persistence.entity.SysUserEntity;
import kr.co.aim.infra.persistence.mapper.SysUserMapper;
import kr.co.aim.infra.persistence.springdatajpa.SysUserJpaRepository;
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

import static kr.co.aim.infra.persistence.entity.QSysUserEntity.sysUserEntity;

/**
 * UserRepository의 JPA 기반 어댑터 구현체.
 * 실제 DB 작업은 Spring Data JPA가 제공하는 UserJpaRepository에 위임합니다.
 */
@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    private final SysUserJpaRepository sysUserJpaRepository;
    private final SysUserMapper sysUserMapper;
    private final JPAQueryFactory queryFactory;

    @Override
    public List<SysUser> findAll() {
        List<SysUserEntity> entities = sysUserJpaRepository.findAll();
        List<SysUser> result = new ArrayList<>();
        for (SysUserEntity entity : entities) {
            result.add(sysUserMapper.toDomain(entity));
        }
        return result;
    }

    @Override
    public Optional<SysUser> findById(Long id) {
        Optional<SysUserEntity> entityOptional = sysUserJpaRepository.findById(id);
        if (entityOptional.isPresent()) {
            return Optional.ofNullable(sysUserMapper.toDomain(entityOptional.get()));
        }
        return Optional.empty();
    }

    @Override
    public Optional<SysUser> findByFactoryNameAndUserId(String factoryName, String userId) {
        Optional<SysUserEntity> entityOptional = sysUserJpaRepository.findByFactoryNameAndUserId(factoryName, userId);
        if (entityOptional.isPresent()) {
            return Optional.ofNullable(sysUserMapper.toDomain(entityOptional.get()));
        }
        return Optional.empty();
    }

    @Override
    public List<SysUser> findByFactoryName(String factoryName) {
        List<SysUserEntity> entities = sysUserJpaRepository.findByFactoryName(factoryName);
        List<SysUser> result = new ArrayList<>();
        for (SysUserEntity entity : entities) {
            result.add(sysUserMapper.toDomain(entity));
        }
        return result;
    }

    @Override
    public SysUser save(SysUser sysUser) {
        SysUserEntity entity = sysUserMapper.toEntity(sysUser);
        SysUserEntity savedEntity = sysUserJpaRepository.save(entity);
        return sysUserMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(Long id) {
        sysUserJpaRepository.deleteById(id);
    }

    @Override
    public void deleteAllByIdInBatch(List<Long> ids) {
        sysUserJpaRepository.deleteAllByIdInBatch(ids);
    }

    @Override
    public Page<SysUser> findUserWithConditions(SysUserSearchCondition condition, Pageable pageable) {
        JPAQuery<SysUserEntity> query = queryFactory
                .selectFrom(sysUserEntity)
                .where(
                        factoryNameContains(condition.getFactoryName()),
                        userIdContains(condition.getUserId()),
                        userNameContains(condition.getUserName()),
                        departmentIdEq(condition.getDepartmentId()),
                        emailContains(condition.getEmail()),
                        phoneNumberContains(condition.getPhoneNumber()),
                        userStateContains(condition.getUserState())
                );

        query.orderBy(getOrderSpecifiers(pageable.getSort()));

        if (pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        List<SysUserEntity> content = query.fetch();
        List<SysUser> converted = new ArrayList<>();
        for (SysUserEntity entity : content) {
            converted.add(sysUserMapper.toDomain(entity));
        }

        long total;
        if (pageable.isPaged()) {
            Long count = queryFactory
                    .select(sysUserEntity.count())
                    .from(sysUserEntity)
                    .where(
                            factoryNameContains(condition.getFactoryName()),
                            userIdContains(condition.getUserId()),
                            userNameContains(condition.getUserName()),
                            departmentIdEq(condition.getDepartmentId()),
                            emailContains(condition.getEmail()),
                            phoneNumberContains(condition.getPhoneNumber()),
                            userStateContains(condition.getUserState())
                    )
                    .fetchOne();

            total = (count != null) ? count.longValue() : 0L;
        } else {
            total = converted.size();
        }

        return new PageImpl<>(converted, pageable, total);
    }

    private OrderSpecifier<?>[] getOrderSpecifiers(Sort sort) {
        List<OrderSpecifier> orders = new ArrayList<>();

        if (sort.isSorted()) {
            for (Sort.Order order : sort) {
                Order direction = order.isAscending() ? Order.ASC : Order.DESC;
                PathBuilder pathBuilder = new PathBuilder<>(sysUserEntity.getType(), sysUserEntity.getMetadata());
                orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
            }
        }

        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(Order.DESC, sysUserEntity.id));
        }

        return orders.toArray(new OrderSpecifier[0]);
    }

    private BooleanExpression factoryNameContains(String factoryName) {
        return StringUtils.hasText(factoryName) ? sysUserEntity.factoryName.contains(factoryName) : null;
    }

    private BooleanExpression userIdContains(String userId) {
        return StringUtils.hasText(userId) ? sysUserEntity.userId.contains(userId) : null;
    }

    private BooleanExpression userNameContains(String userName) {
        return StringUtils.hasText(userName) ? sysUserEntity.userName.contains(userName) : null;
    }

    private BooleanExpression departmentIdEq(Long departmentId) {
        return departmentId != null ? sysUserEntity.departmentId.eq(departmentId) : null;
    }

    private BooleanExpression emailContains(String email) {
        return StringUtils.hasText(email) ? sysUserEntity.email.contains(email) : null;
    }

    private BooleanExpression phoneNumberContains(String phoneNumber) {
        return StringUtils.hasText(phoneNumber) ? sysUserEntity.phoneNumber.contains(phoneNumber) : null;
    }

    private BooleanExpression userStateContains(String userState) {
        return StringUtils.hasText(userState) ? sysUserEntity.userState.contains(userState) : null;
    }
}

