package kr.co.aim.infra.persistence.adapter;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.domain.model.UserGroupMember;
import kr.co.aim.domain.repository.UserGroupMemberRepository;
import kr.co.aim.infra.persistence.entity.UserGroupMemberEntity;
import kr.co.aim.infra.persistence.mapper.UserGroupMemberMapper;
import kr.co.aim.infra.persistence.springdatajpa.UserGroupMemberJpaRepository;
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
import static kr.co.aim.infra.persistence.entity.QUserGroupEntity.userGroupEntity;
import static kr.co.aim.infra.persistence.entity.QUserGroupMemberEntity.userGroupMemberEntity;

/**
 * UserGroupMemberRepository의 JPA 기반 어댑터 구현체.
 * 실제 DB 작업은 Spring Data JPA가 제공하는 UserGroupMemberJpaRepository에 위임합니다.
 */
@Repository
@RequiredArgsConstructor
public class UserGroupMemberRepositoryAdapter implements UserGroupMemberRepository {

    private final UserGroupMemberJpaRepository userGroupMemberJpaRepository;
    private final UserGroupMemberMapper userGroupMemberMapper;
    private final JPAQueryFactory queryFactory;

    @Override
    public List<UserGroupMember> findAll() {
        List<UserGroupMemberEntity> entities = userGroupMemberJpaRepository.findAll();
        List<UserGroupMember> result = new ArrayList<>();
        for (UserGroupMemberEntity entity : entities) {
            result.add(userGroupMemberMapper.toDomain(entity));
        }
        return result;
    }

    @Override
    public Optional<UserGroupMember> findById(Long id) {
        Optional<UserGroupMemberEntity> entityOptional = userGroupMemberJpaRepository.findById(id);
        if (entityOptional.isPresent()) {
            return Optional.ofNullable(userGroupMemberMapper.toDomain(entityOptional.get()));
        }
        return Optional.empty();
    }

    @Override
    public List<UserGroupMember> findByUserId(Long userId) {
        List<UserGroupMemberEntity> entities = userGroupMemberJpaRepository.findByUserId(userId);
        List<UserGroupMember> result = new ArrayList<>();
        for (UserGroupMemberEntity entity : entities) {
            result.add(userGroupMemberMapper.toDomain(entity));
        }
        return result;
    }

    @Override
    public List<UserGroupMember> findByUserGroupId(Long userGroupId) {
        List<UserGroupMemberEntity> entities = userGroupMemberJpaRepository.findByUserGroupId(userGroupId);
        List<UserGroupMember> result = new ArrayList<>();
        for (UserGroupMemberEntity entity : entities) {
            result.add(userGroupMemberMapper.toDomain(entity));
        }
        return result;
    }

    @Override
    public Optional<UserGroupMember> findByUserGroupIdAndUserId(Long userGroupId, Long userId) {
        Optional<UserGroupMemberEntity> entityOptional = userGroupMemberJpaRepository.findByUserGroupIdAndUserId(userGroupId, userId);
        if (entityOptional.isPresent()) {
            return Optional.ofNullable(userGroupMemberMapper.toDomain(entityOptional.get()));
        }
        return Optional.empty();
    }

    @Override
    public UserGroupMember save(UserGroupMember userGroupMember) {
        UserGroupMemberEntity entity = userGroupMemberMapper.toEntity(userGroupMember);
        UserGroupMemberEntity savedEntity = userGroupMemberJpaRepository.save(entity);
        return userGroupMemberMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(Long id) {
        userGroupMemberJpaRepository.deleteById(id);
    }

    @Override
    public void deleteAllByIdInBatch(List<Long> ids) {
        userGroupMemberJpaRepository.deleteAllByIdInBatch(ids);
    }

    @Override
    public Page<UserGroupMember> findUserGroupMembersWithDetails(Long userId, Long userGroupId, String factoryName, Pageable pageable) {
        JPAQuery<Tuple> query = queryFactory
                .select(
                        userGroupMemberEntity,
                        sysUserEntity.userId,
                        sysUserEntity.userName,
                        userGroupEntity.userGroupName,
                        userGroupEntity.description
                )
                .from(userGroupMemberEntity)
                .leftJoin(sysUserEntity).on(userGroupMemberEntity.userId.eq(sysUserEntity.id))
                .leftJoin(userGroupEntity).on(userGroupMemberEntity.userGroupId.eq(userGroupEntity.id))
                .where(
                        userIdEq(userId),
                        userGroupIdEq(userGroupId),
                        factoryNameContains(factoryName)
                );

        query.orderBy(getOrderSpecifiers(pageable.getSort()));

        if (pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        List<Tuple> content = query.fetch();
        List<UserGroupMember> converted = new ArrayList<>();
        for (Tuple tuple : content) {
            UserGroupMemberEntity entity = tuple.get(userGroupMemberEntity);
            if (entity != null) {
                UserGroupMember domain = userGroupMemberMapper.toDomain(entity);
                domain.setEmployeeId(tuple.get(sysUserEntity.userId));
                domain.setUserName(tuple.get(sysUserEntity.userName));
                domain.setUserGroupName(tuple.get(userGroupEntity.userGroupName));
                domain.setGroupDescription(tuple.get(userGroupEntity.description));
                converted.add(domain);
            }
        }

        long total;
        if (pageable.isPaged()) {
            Long count = queryFactory
                    .select(userGroupMemberEntity.count())
                    .from(userGroupMemberEntity)
                    .leftJoin(sysUserEntity).on(userGroupMemberEntity.userId.eq(sysUserEntity.id))
                    .leftJoin(userGroupEntity).on(userGroupMemberEntity.userGroupId.eq(userGroupEntity.id))
                    .where(
                            userIdEq(userId),
                            userGroupIdEq(userGroupId),
                            factoryNameContains(factoryName)
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
                PathBuilder pathBuilder = new PathBuilder<>(userGroupMemberEntity.getType(), userGroupMemberEntity.getMetadata());
                orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
            }
        }

        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(Order.DESC, userGroupMemberEntity.id));
        }

        return orders.toArray(new OrderSpecifier[0]);
    }

    private BooleanExpression userIdEq(Long userId) {
        return userId != null ? userGroupMemberEntity.userId.eq(userId) : null;
    }

    private BooleanExpression userGroupIdEq(Long userGroupId) {
        return userGroupId != null ? userGroupMemberEntity.userGroupId.eq(userGroupId) : null;
    }

    private BooleanExpression factoryNameContains(String factoryName) {
        return StringUtils.hasText(factoryName) ? userGroupMemberEntity.factoryName.contains(factoryName) : null;
    }
}

