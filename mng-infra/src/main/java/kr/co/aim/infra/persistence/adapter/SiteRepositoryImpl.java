package kr.co.aim.infra.persistence.adapter;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.domain.model.Site;
import kr.co.aim.domain.repository.SiteRepository;
import kr.co.aim.domain.repository.condition.SiteSearchCondition;
import kr.co.aim.infra.persistence.entity.SiteEntity;
import kr.co.aim.infra.persistence.mapper.SiteMapper;
import kr.co.aim.infra.persistence.springdatajpa.SiteJpaRepository;
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

import static kr.co.aim.infra.persistence.entity.QSiteEntity.siteEntity;

/**
 * SiteRepository의 JPA 기반 어댑터 구현체.
 * 실제 DB 작업은 Spring Data JPA가 제공하는 SiteJpaRepository 및 QueryDSL에 위임합니다.
 */
@Repository
@RequiredArgsConstructor
public class SiteRepositoryImpl implements SiteRepository {

    private final SiteJpaRepository siteJpaRepository;
    private final SiteMapper siteMapper;
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Site> findAll() {
        List<SiteEntity> entities = siteJpaRepository.findAll();
        List<Site> result = new ArrayList<>();
        for (SiteEntity entity : entities) {
            result.add(siteMapper.toDomain(entity));
        }
        return result;
    }

    @Override
    public Optional<Site> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        Optional<SiteEntity> entityOptional = siteJpaRepository.findById(id);
        if (entityOptional.isPresent()) {
            return Optional.ofNullable(siteMapper.toDomain(entityOptional.get()));
        }
        return Optional.empty();
    }

    @Override
    public Optional<Site> findBySiteName(String siteName) {
        if (!StringUtils.hasText(siteName)) {
            return Optional.empty();
        }
        Optional<SiteEntity> entityOptional = siteJpaRepository.findBySiteName(siteName);
        if (entityOptional.isPresent()) {
            return Optional.ofNullable(siteMapper.toDomain(entityOptional.get()));
        }
        return Optional.empty();
    }

    @Override
    public Site save(Site site) {
        SiteEntity entity = siteMapper.toEntity(site);
        SiteEntity savedEntity = siteJpaRepository.save(entity);
        return siteMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(Long id) {
        siteJpaRepository.deleteById(id);
    }

    @Override
    public void deleteAllByIdInBatch(List<Long> ids) {
        siteJpaRepository.deleteAllByIdInBatch(ids);
    }

    @Override
    public Page<Site> findSitesWithConditions(SiteSearchCondition condition, Pageable pageable) {
        JPAQuery<SiteEntity> query = queryFactory
                .select(siteEntity)
                .from(siteEntity)
                .where(
                        siteNameContains(condition.getSiteName()),
                        descriptionContains(condition.getDescription()),
                        eventNameContains(condition.getEventName()),
                        eventUserContains(condition.getEventUser())
                );

        query.orderBy(getOrderSpecifiers(pageable.getSort()));

        if (pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        List<SiteEntity> content = query.fetch();
        List<Site> converted = new ArrayList<>();
        for (SiteEntity entity : content) {
            if (entity != null) {
                Site domain = siteMapper.toDomain(entity);
                converted.add(domain);
            }
        }

        long total;
        if (pageable.isPaged()) {
            Long count = queryFactory
                    .select(siteEntity.count())
                    .from(siteEntity)
                    .where(
                            siteNameContains(condition.getSiteName()),
                            descriptionContains(condition.getDescription()),
                            eventNameContains(condition.getEventName()),
                            eventUserContains(condition.getEventUser())
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

        if (sort != null && sort.isSorted()) {
            for (Sort.Order order : sort) {
                Order direction = order.isAscending() ? Order.ASC : Order.DESC;
                PathBuilder pathBuilder = new PathBuilder<>(siteEntity.getType(), siteEntity.getMetadata());
                orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
            }
        }

        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(Order.DESC, siteEntity.id));
        }

        return orders.toArray(new OrderSpecifier[0]);
    }

    private BooleanExpression siteNameContains(String siteName) {
        return StringUtils.hasText(siteName) ? siteEntity.siteName.contains(siteName) : null;
    }

    private BooleanExpression descriptionContains(String description) {
        return StringUtils.hasText(description) ? siteEntity.description.contains(description) : null;
    }

    private BooleanExpression eventNameContains(String eventName) {
        return StringUtils.hasText(eventName) ? siteEntity.eventName.contains(eventName) : null;
    }

    private BooleanExpression eventUserContains(String eventUser) {
        return StringUtils.hasText(eventUser) ? siteEntity.eventUser.contains(eventUser) : null;
    }
}
