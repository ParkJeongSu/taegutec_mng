package kr.co.aim.infra.persistence.adapter;

import com.querydsl.core.Tuple;
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

import static kr.co.aim.infra.persistence.entity.QDepartmentEntity.departmentEntity;
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
        if (id == null) {
            return Optional.empty();
        }
        Tuple tuple = queryFactory
                .select(sysUserEntity, departmentEntity.departmentName)
                .from(sysUserEntity)
                .leftJoin(departmentEntity).on(sysUserEntity.departmentId.eq(departmentEntity.id))
                .where(sysUserEntity.id.eq(id))
                .fetchOne();

        if (tuple == null) {
            return Optional.empty();
        }

        SysUserEntity entity = tuple.get(sysUserEntity);
        if (entity == null) {
            return Optional.empty();
        }

        SysUser domain = sysUserMapper.toDomain(entity);
        domain.setDepartmentName(tuple.get(departmentEntity.departmentName));
        return Optional.of(domain);
    }

    @Override
    public Optional<SysUser> findByFactoryNameAndUserId(String factoryName, String userId) {
        Tuple tuple = queryFactory
                .select(sysUserEntity, departmentEntity.departmentName)
                .from(sysUserEntity)
                .leftJoin(departmentEntity).on(sysUserEntity.departmentId.eq(departmentEntity.id))
                .where(
                        sysUserEntity.factoryName.eq(factoryName),
                        sysUserEntity.userId.eq(userId)
                )
                .fetchOne();

        if (tuple == null) {
            return Optional.empty();
        }

        SysUserEntity entity = tuple.get(sysUserEntity);
        if (entity == null) {
            return Optional.empty();
        }

        SysUser domain = sysUserMapper.toDomain(entity);
        domain.setDepartmentName(tuple.get(departmentEntity.departmentName));
        return Optional.of(domain);
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
        SysUser domain = sysUserMapper.toDomain(savedEntity);
        domain.setDepartmentName(sysUser.getDepartmentName());
        return domain;
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
        JPAQuery<Tuple> query = queryFactory
                .select(sysUserEntity, departmentEntity.departmentName)
                .from(sysUserEntity)
                .leftJoin(departmentEntity).on(sysUserEntity.departmentId.eq(departmentEntity.id))
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

        List<Tuple> content = query.fetch();
        List<SysUser> converted = new ArrayList<>();
        for (Tuple tuple : content) {
            SysUserEntity entity = tuple.get(sysUserEntity);
            if (entity != null) {
                SysUser domain = sysUserMapper.toDomain(entity);
                domain.setDepartmentName(tuple.get(departmentEntity.departmentName));
                converted.add(domain);
            }
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

