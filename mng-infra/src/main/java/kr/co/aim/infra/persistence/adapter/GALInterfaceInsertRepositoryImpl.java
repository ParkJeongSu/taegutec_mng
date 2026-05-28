package kr.co.aim.infra.persistence.adapter;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
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
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static kr.co.aim.infra.persistence.db2entity.insert.QIdocEntity.idocEntity;
import static kr.co.aim.infra.persistence.db2entity.insert.QH2OrderMEntity.h2OrderMEntity;
import static kr.co.aim.infra.persistence.db2entity.insert.QH2OrderDEntity.h2OrderDEntity;
import static kr.co.aim.infra.persistence.db2entity.insert.QH2TransEntity.h2TransEntity;


@Repository
@ConditionalOnProperty(name = "factory.type", havingValue = "insert")
@Profile({"scheduler","simulator","web"})
public class GALInterfaceInsertRepositoryImpl implements GALInterfaceRepository {
    private final JPAQueryFactory queryFactory; // ✨ JPAQueryFactory 주입

    // 생성자를 직접 쓰면 @Qualifier를 가장 정확하게 제어할 수 있습니다.
    public GALInterfaceInsertRepositoryImpl(@Qualifier("db2QueryFactory") JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<GALInterfaceResponse> getInterfaceList(GALInterfaceSearchCondition condition, Pageable pageable) {
        //1. 공통 쿼리 빌더 생성 (SELECT, FROM, JOIN, WHERE)
        JPAQuery<GALInterfaceResponse> query = queryFactory
                .select(Projections.fields(GALInterfaceResponse.class,
                        // === IDOC (Header) 필드 ===
                        idocEntity.lineId.as("lineId"),
                        idocEntity.idocTypId.as("idocTypId"),
                        idocEntity.state.as("state"),
                        idocEntity.errorCode.as("errorCode"),
                        idocEntity.source.as("source"),
                        idocEntity.destination.as("destination"),
                        idocEntity.tidId.as("tidId"),
                        idocEntity.docNum.as("docNum"),
                        idocEntity.queueName.as("queueName"),
                        idocEntity.partnerType.as("partnerType"),
                        idocEntity.partnerName.as("partnerName"),
                        idocEntity.partnerPort.as("partnerPort"),
                        idocEntity.msgVariant.as("msgVariant"),
                        idocEntity.arcKey.as("arcKey"),
                        idocEntity.dtimeCre.as("dtimeCre"),
                        idocEntity.dtimeMod.as("dtimeMod"),
                        idocEntity.usrMod.as("usrMod"),
                        idocEntity.pgmMod.as("pgmMod"),
                        idocEntity.modCnt.as("modCnt"),

                        // === H2ORDERM (Master) 필드 ===
                        h2OrderMEntity.dataCode.as("dataCode"),
                        h2OrderMEntity.bookCtrl.as("bookCtrl"),
                        h2OrderMEntity.cClient.as("cClient"),
                        h2OrderMEntity.cOrderId.as("cOrderId"),
                        h2OrderMEntity.cOrderTy.as("cOrderTy"),
                        h2OrderMEntity.cDtPick.as("cDtPick"),
                        h2OrderMEntity.cOrderPrio.as("cOrderPrio"),
                        h2OrderMEntity.cTCode.as("cTCode"),
                        h2OrderMEntity.cLocId.as("cLocId"),
                        h2OrderMEntity.cWcId.as("cWcId"),
                        h2OrderMEntity.cGalId.as("cGalId"),
                        h2OrderMEntity.cGalWhs.as("cGalWhs"),
                        h2OrderMEntity.cHostUsr.as("cHostUsr"),
                        h2OrderMEntity.cUsrNo.as("cUsrNo"),

                        // === H2TRANS (Result) 필드 ===
                        h2TransEntity.cTransTy.as("cTransTy"),
                        h2TransEntity.cErrId.as("cErrId"),
                        h2TransEntity.cText1.as("cText1"),
                        h2TransEntity.cOrderLn.as("cOrderLn"),
                        h2TransEntity.cGaId.as("cGaId"),
                        h2TransEntity.cCoId.as("cCoId"),
                        h2TransEntity.cGrWgAct.as("cGrWgAct"),
                        h2TransEntity.cReqZone.as("cReqZone"),
                        h2TransEntity.cZone.as("cZone"),
                        h2TransEntity.cErrDsc.as("cErrDsc")
                        // Powder 전용 필드들은 매핑하지 않음 (자동으로 null 처리됨)
                ))
                .from(idocEntity)
                .leftJoin(h2OrderMEntity)
                .leftJoin(h2TransEntity)
                .where(
                        // (WHERE 조건이 있다면 여기에 추가)
                );

        // 2. 정렬 적용
        query.orderBy(getOrderSpecifiers(pageable.getSort()));

        // 3. 페이징 적용 (isPaged()로 분기)
        if (pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        // 4. 데이터 조회
        List<GALInterfaceResponse> content = query.fetch();

        // 5. 카운트 조회 (isPaged()로 분기)
        long total;
        if (pageable.isPaged()) {
            // [페이징 O] 별도 카운트 쿼리 실행
            Long count = queryFactory
                    .select(idocEntity.lineId.countDistinct())
                    .from(idocEntity)
                    .leftJoin(h2OrderMEntity)
                    .leftJoin(h2TransEntity)
                    .where(
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

    @Override
    public Page<GALDetailInterfaceResponse> getDetailInterfaceList(GALDetailInterfaceSearchCondition condition, Pageable pageable) {
        // H2ORDERD 상세 조회 로직 (필요 시 유사한 방식으로 구현)
        JPAQuery<GALDetailInterfaceResponse> query = queryFactory
                .select(Projections.fields(GALDetailInterfaceResponse.class,
                        h2OrderDEntity.lineId.as("lineId"),
                        h2OrderDEntity.idocId.as("idocId"),
                        h2OrderDEntity.dtimeCre.as("dtimeCre"),
                        h2OrderDEntity.dtimeMod.as("dtimeMod"),
                        h2OrderDEntity.usrMod.as("usrMod"),
                        h2OrderDEntity.pgmMod.as("pgmMod"),
                        h2OrderDEntity.modCnt.as("modCnt"),
                        h2OrderDEntity.dataCode.as("dataCode"),
                        h2OrderDEntity.cClient.as("cClient"),
                        h2OrderDEntity.cOrderId.as("cOrderId"),
                        h2OrderDEntity.cOrderTy.as("cOrderTy"),
                        h2OrderDEntity.cOrderLn.as("cOrderLn"),
                        h2OrderDEntity.cCoId.as("cCoId"),
                        h2OrderDEntity.cCoTy.as("cCoTy"),
                        h2OrderDEntity.cZone.as("cZone"),
                        h2OrderDEntity.cDrivingProfile.as("cDrivingProfile")
                ))
                .from(h2OrderDEntity)
                .where(

                );

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
                    .select(h2OrderDEntity.lineId.count())
                    .from(h2OrderDEntity)
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
        return null;
    }

    private OrderSpecifier<?>[] getOrderSpecifiers(Sort sort) {
        List<OrderSpecifier> orders = new ArrayList<>();

        if (sort.isSorted()) {
            for (Sort.Order order : sort) {
                // 정렬 방향을 결정합니다 (ASC or DESC)
                Order direction = order.isAscending() ? Order.ASC : Order.DESC;

                // 정렬할 속성(컬럼)을 PathBuilder를 통해 지정합니다.
                // "userName"과 같은 문자열을 Q-Type 경로로 변환해줍니다.
                PathBuilder pathBuilder = new PathBuilder<>(idocEntity.getType(), idocEntity.getMetadata());

                orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
            }
        }

        // 기본 정렬 조건 (만약 정렬 조건이 없다면 id 내림차순)
        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(Order.DESC, idocEntity.lineId));
        }

        return orders.toArray(new OrderSpecifier[0]);
    }

    private OrderSpecifier<?>[] getOrderSpecifiersDetail(Sort sort) {
        List<OrderSpecifier> orders = new ArrayList<>();
        if (sort.isSorted()) {
            // 람다식을 사용하지 않는 표준 for 루프 사용
            for (Sort.Order order : sort) {
                Order direction = order.isAscending() ? Order.ASC : Order.DESC;
                PathBuilder pathBuilder = new PathBuilder<>(h2OrderDEntity.getType(), h2OrderDEntity.getMetadata());
                orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
            }
        }
        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(Order.DESC, h2OrderDEntity.lineId));
        }
        return orders.toArray(new OrderSpecifier[0]);
    }
}
