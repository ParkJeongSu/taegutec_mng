package kr.co.aim.infra.persistence.adapter;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.common.condition.DepartmentSearchCondition;
import kr.co.aim.domain.model.Department;
import kr.co.aim.domain.repository.DepartmentRepository;
import kr.co.aim.infra.persistence.entity.DepartmentEntity;
import kr.co.aim.infra.persistence.mapper.DepartmentMapper;
import kr.co.aim.infra.persistence.springdatajpa.DepartmentJpaRepository;
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

/**
 * DepartmentRepository의 JPA 기반 어댑터 구현체.
 * 실제 DB 작업은 Spring Data JPA가 제공하는 DepartmentJpaRepository에 위임합니다.
 */
@Repository
@RequiredArgsConstructor
public class DepartmentRepositoryImpl implements DepartmentRepository {

    private final DepartmentJpaRepository departmentJpaRepository;
    private final DepartmentMapper departmentMapper;
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Department> findAll() {
        List<DepartmentEntity> entities = departmentJpaRepository.findAll();
        List<Department> result = new ArrayList<>();
        for (DepartmentEntity entity : entities) {
            result.add(departmentMapper.toDomain(entity));
        }
        return result;
    }

    @Override
    public Optional<Department> findById(Long id) {
        Optional<DepartmentEntity> entityOptional = departmentJpaRepository.findById(id);
        if (entityOptional.isPresent()) {
            return Optional.ofNullable(departmentMapper.toDomain(entityOptional.get()));
        }
        return Optional.empty();
    }

    @Override
    public Optional<Department> findByFactoryNameAndDepartmentName(String factoryName, String departmentName) {
        Optional<DepartmentEntity> entityOptional = departmentJpaRepository.findByFactoryNameAndDepartmentName(factoryName, departmentName);
        if (entityOptional.isPresent()) {
            return Optional.ofNullable(departmentMapper.toDomain(entityOptional.get()));
        }
        return Optional.empty();
    }

    @Override
    public List<Department> findByFactoryName(String factoryName) {
        List<DepartmentEntity> entities = departmentJpaRepository.findByFactoryName(factoryName);
        List<Department> result = new ArrayList<>();
        for (DepartmentEntity entity : entities) {
            result.add(departmentMapper.toDomain(entity));
        }
        return result;
    }

    @Override
    public Department save(Department department) {
        DepartmentEntity entity = departmentMapper.toEntity(department);
        DepartmentEntity savedEntity = departmentJpaRepository.save(entity);
        return departmentMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(Long id) {
        departmentJpaRepository.deleteById(id);
    }

    @Override
    public void deleteAllByIdInBatch(List<Long> ids) {
        departmentJpaRepository.deleteAllByIdInBatch(ids);
    }

    @Override
    public Page<Department> findDepartments(DepartmentSearchCondition condition, Pageable pageable) {
        JPAQuery<DepartmentEntity> query = queryFactory
                .select(departmentEntity)
                .from(departmentEntity)
                .where(
                        factoryNameContains(condition.getFactoryName()),
                        departmentNameContains(condition.getDepartmentName()),
                        useStateContains(condition.getUseState())
                );

        query.orderBy(getOrderSpecifiers(pageable.getSort()));

        if (pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        List<DepartmentEntity> content = query.fetch();
        List<Department> converted = new ArrayList<>();
        for (DepartmentEntity entity : content) {
            if (entity != null) {
                Department domain = departmentMapper.toDomain(entity);
                converted.add(domain);
            }
        }

        long total;
        if (pageable.isPaged()) {
            Long count = queryFactory
                    .select(departmentEntity.count())
                    .from(departmentEntity)
                    .where(
                            factoryNameContains(condition.getFactoryName()),
                            departmentNameContains(condition.getDepartmentName()),
                            useStateContains(condition.getUseState())
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
                PathBuilder pathBuilder = new PathBuilder<>(departmentEntity.getType(), departmentEntity.getMetadata());
                orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
            }
        }

        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(Order.DESC, departmentEntity.id));
        }

        return orders.toArray(new OrderSpecifier[0]);
    }

    private BooleanExpression factoryNameContains(String factoryName) {
        return StringUtils.hasText(factoryName) ? departmentEntity.factoryName.contains(factoryName) : null;
    }

    private BooleanExpression departmentNameContains(String departmentName) {
        return StringUtils.hasText(departmentName) ? departmentEntity.departmentName.contains(departmentName) : null;
    }

    private BooleanExpression useStateContains(String useState) {
        return StringUtils.hasText(useState) ? departmentEntity.useState.contains(useState) : null;
    }
}
