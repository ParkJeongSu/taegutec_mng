package kr.co.aim.infra.persistence.adapter;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.Column;
import kr.co.aim.common.condition.GALDetailInterfaceSearchCondition;
import kr.co.aim.common.condition.GALInterfaceSearchCondition;
import kr.co.aim.common.condition.GALPartSearchCondition;
import kr.co.aim.domain.model.GALDetailInterfaceResponse;
import kr.co.aim.domain.model.GALInterfaceResponse;
import kr.co.aim.domain.model.GALPartResponse;
import kr.co.aim.domain.repository.GALInterfaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


import static kr.co.aim.infra.persistence.db2entity.powder.QIdocPEntity.idocPEntity;
import static kr.co.aim.infra.persistence.db2entity.powder.QH2OrderMPEntity.h2OrderMPEntity;
import static kr.co.aim.infra.persistence.db2entity.powder.QH2OrderDPEntity.h2OrderDPEntity;
import static kr.co.aim.infra.persistence.db2entity.powder.QH2TransPEntity.h2TransPEntity;
import static kr.co.aim.infra.persistence.db2entity.powder.QH2PartMPEntity.h2PartMPEntity;

@Repository
@ConditionalOnProperty(name = "factory.type", havingValue = "powder")
public class GALInterfacePowderRepositoryImpl implements GALInterfaceRepository {
    private final JPAQueryFactory queryFactory; // ✨ JPAQueryFactory 주입

    // 생성자를 직접 쓰면 @Qualifier를 가장 정확하게 제어할 수 있습니다.
    public GALInterfacePowderRepositoryImpl(@Qualifier("db2QueryFactory") JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<GALInterfaceResponse> getInterfaceList(GALInterfaceSearchCondition condition, Pageable pageable) {
        // 1. 쿼리 생성 및 Powder 전용 필드 매핑
        JPAQuery<GALInterfaceResponse> query = queryFactory
                .select(Projections.fields(GALInterfaceResponse.class,
                        // === IDOC (Common Header) 필드 ===
                        idocPEntity.lineId.as("lineId"),
                        idocPEntity.idocTypId.as("idocTypId"),
                        idocPEntity.state.as("state"),
                        idocPEntity.errorCode.as("errorCode"),
                        idocPEntity.source.as("source"),
                        idocPEntity.destination.as("destination"),
                        idocPEntity.dtimeCre.as("dtimeCre"),
                        idocPEntity.dtimeMod.as("dtimeMod"),
                        idocPEntity.usrMod.as("usrMod"),
                        idocPEntity.pgmMod.as("pgmMod"),
                        idocPEntity.modCnt.as("modCnt"),

                        // === H2ORDERMP (Master) 필드 - Powder 전용 ===
                        h2OrderMPEntity.cOrderTy.as("cOrderTy"),
                        h2OrderMPEntity.fromWhCd.as("fromWhCd"),
                        h2OrderMPEntity.toWhCd.as("toWhCd"),

                        // === H2TRANSP (Result) 필드 - Powder 전용 ===
                        h2TransPEntity.cOrderId.as("cOrderId"),
                        h2TransPEntity.rrn.as("rrn"),
                        h2TransPEntity.lineNo.as("lineNo"),
                        h2TransPEntity.lot.as("lot"),
                        h2TransPEntity.galKey.as("galKey"),
                        h2TransPEntity.cTransTy.as("cTransTy"),
                        h2TransPEntity.carrierId.as("carrierId"),
                        h2TransPEntity.currRrn.as("currRrn"),
                        h2TransPEntity.nextRrn.as("nextRrn"),
                        h2TransPEntity.actQty.as("actQty"),
                        h2TransPEntity.missQty.as("missQty"),
                        h2TransPEntity.surpQty.as("surpQty"),
                        h2TransPEntity.resultStat.as("resultStat"),
                        h2TransPEntity.errReason.as("errReason"),
                        h2TransPEntity.eventDt.as("eventDt"),
                        h2TransPEntity.h2ordLineId.as("h2ordLineId")
                ))
                .from(idocPEntity)
                // Powder는 lineid와 idocid가 조인 키입니다.
                .leftJoin(h2OrderMPEntity).on(idocPEntity.lineId.eq(h2OrderMPEntity.idocId))
                .leftJoin(h2TransPEntity).on(idocPEntity.lineId.eq(h2TransPEntity.idocId))
                .where(
                        // 검색 조건 처리 (필요 시 condition 객체 활용)
                );

        // 2. 정렬 및 페이징 적용
        query.orderBy(getOrderSpecifiers(pageable.getSort()));
        if (pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        List<GALInterfaceResponse> content = query.fetch();

        // 3. 전체 카운트 조회
        long total;
        if (pageable.isPaged()) {
            Long count = queryFactory
                    .select(idocPEntity.lineId.count())
                    .from(idocPEntity)
                    .leftJoin(h2OrderMPEntity).on(idocPEntity.lineId.eq(h2OrderMPEntity.idocId))
                    .leftJoin(h2TransPEntity).on(idocPEntity.lineId.eq(h2TransPEntity.idocId))
                    .fetchOne();
            total = (count != null) ? count : 0L;
        } else {
            total = content.size();
        }

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<GALDetailInterfaceResponse> getDetailInterfaceList(GALDetailInterfaceSearchCondition condition, Pageable pageable) {
        // H2ORDERDP (Powder 상세) 조회 로직
        JPAQuery<GALDetailInterfaceResponse> query = queryFactory
                .select(Projections.fields(GALDetailInterfaceResponse.class,
                        h2OrderDPEntity.lineId.as("lineId"),
                        h2OrderDPEntity.idocId.as("idocId"),
                        h2OrderDPEntity.dtimeCre.as("dtimeCre"),
                        h2OrderDPEntity.dtimeMod.as("dtimeMod"),
                        h2OrderDPEntity.usrMod.as("usrMod"),
                        h2OrderDPEntity.pgmMod.as("pgmMod"),
                        h2OrderDPEntity.modCnt.as("modCnt"),
                        h2OrderDPEntity.cOrderId.as("cOrderId"),
                        h2OrderDPEntity.rrn.as("rrn"),
                        h2OrderDPEntity.lineNo.as("lineNo"),
                        h2OrderDPEntity.cPartId.as("cPartId"),
                        h2OrderDPEntity.lot.as("lot"),
                        h2OrderDPEntity.qty.as("qty"),
                        h2OrderDPEntity.uom.as("uom"),
                        h2OrderDPEntity.machine.as("machine"),
                        h2OrderDPEntity.currRrn.as("currRrn"),
                        h2OrderDPEntity.nextRrn.as("nextRrn"),
                        h2OrderDPEntity.minReceiveQty.as("minReceiveQty"),
                        h2OrderDPEntity.maxReceiveQty.as("maxReceiveQty"),
                        h2OrderDPEntity.defaultReceiveQty.as("defaultReceiveQty"),
                        h2OrderDPEntity.h2trnLineId.as("h2trnLineId"),
                        h2OrderDPEntity.galKey.as("galKey")
                ))
                .from(h2OrderDPEntity)
                .where();

        // 2. 정렬 및 페이징 적용
        query.orderBy(getOrderSpecifiersDetail(pageable.getSort()));
        if (pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        List<GALDetailInterfaceResponse> content = query.fetch();

        // 3. 전체 카운트 조회
        long total;
        if (pageable.isPaged()) {
            Long count = queryFactory
                    .select(h2OrderDPEntity.lineId.count())
                    .from(h2OrderDPEntity)
                    .where()
                    .fetchOne();
            total = (count != null) ? count : 0L;
        } else {
            total = content.size();
        }

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<GALPartResponse> getPartList(GALPartSearchCondition condition, Pageable pageable) {
        // H2ORDERDP (Powder 상세) 조회 로직
        JPAQuery<GALPartResponse> query = queryFactory
                .select(Projections.fields(GALPartResponse.class,
                        h2PartMPEntity.lineId.as("lineId"),
                        h2PartMPEntity.idocId.as("idocId"),
                        h2PartMPEntity.dtimeCre.as("dtimeCre"),
                        h2PartMPEntity.dtimeMod.as("dtimeMod"),
                        h2PartMPEntity.usrMod.as("usrMod"),
                        h2PartMPEntity.pgmMod.as("pgmMod"),
                        h2PartMPEntity.modCnt.as("modCnt"),
                        h2PartMPEntity.cPartId.as("cPartId"),
                        h2PartMPEntity.cPartDsc.as("cPartDsc"),
                        h2PartMPEntity.cPartDsc2.as("cPartDsc2"),
                        h2PartMPEntity.cratIo.as("cratIo")
                ))
                .from(h2PartMPEntity)
                .where();

        // 2. 정렬 및 페이징 적용
        query.orderBy(getOrderSpecifiersPart(pageable.getSort()));
        if (pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        List<GALPartResponse> content = query.fetch();

        // 3. 전체 카운트 조회
        long total;
        if (pageable.isPaged()) {
            Long count = queryFactory
                    .select(h2PartMPEntity.lineId.count())
                    .from(h2PartMPEntity)
                    .where()
                    .fetchOne();
            total = (count != null) ? count : 0L;
        } else {
            total = content.size();
        }

        return new PageImpl<>(content, pageable, total);
    }

    private OrderSpecifier<?>[] getOrderSpecifiers(Sort sort) {
        List<OrderSpecifier> orders = new ArrayList<>();
        if (sort.isSorted()) {
            // 람다식을 사용하지 않는 표준 for 루프 사용
            for (Sort.Order order : sort) {
                Order direction = order.isAscending() ? Order.ASC : Order.DESC;
                PathBuilder pathBuilder = new PathBuilder<>(idocPEntity.getType(), idocPEntity.getMetadata());
                orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
            }
        }
        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(Order.DESC, idocPEntity.lineId));
        }
        return orders.toArray(new OrderSpecifier[0]);
    }

    private OrderSpecifier<?>[] getOrderSpecifiersDetail(Sort sort) {
        List<OrderSpecifier> orders = new ArrayList<>();
        if (sort.isSorted()) {
            // 람다식을 사용하지 않는 표준 for 루프 사용
            for (Sort.Order order : sort) {
                Order direction = order.isAscending() ? Order.ASC : Order.DESC;
                PathBuilder pathBuilder = new PathBuilder<>(h2OrderDPEntity.getType(), h2OrderDPEntity.getMetadata());
                orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
            }
        }
        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(Order.DESC, h2OrderDPEntity.lineId));
        }
        return orders.toArray(new OrderSpecifier[0]);
    }

    private OrderSpecifier<?>[] getOrderSpecifiersPart(Sort sort) {
        List<OrderSpecifier> orders = new ArrayList<>();
        if (sort.isSorted()) {
            // 람다식을 사용하지 않는 표준 for 루프 사용
            for (Sort.Order order : sort) {
                Order direction = order.isAscending() ? Order.ASC : Order.DESC;
                PathBuilder pathBuilder = new PathBuilder<>(h2PartMPEntity.getType(), h2PartMPEntity.getMetadata());
                orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
            }
        }
        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(Order.DESC, h2PartMPEntity.cPartId));
        }
        return orders.toArray(new OrderSpecifier[0]);
    }



}
