package kr.co.aim.infra.persistence.db2springdatajpa.powder;

import kr.co.aim.common.dto.powder.IdocH2PartMResponseDto;
import kr.co.aim.common.dto.powder.IdocH2TransResponseDto;
import kr.co.aim.common.dto.powder.IdocOrderMasterResponseDto;
import kr.co.aim.infra.persistence.db2entity.powder.IdocPEntity;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@ConditionalOnProperty(name = "factory.type", havingValue = "powder")
public interface IdocPJpaRepository extends JpaRepository<IdocPEntity, Long> {
    List<IdocPEntity> findByState(Long state);
    Optional<IdocPEntity> findByLineId(Long lineId);
    Page<IdocPEntity> findAll(Pageable pageable);
//    @Query("SELECT COALESCE(MAX(i.lineId), 0) +1 FROM IdocPEntity i")
//    Long findMaxLineId();
    @Query(value = "SELECT NEXT VALUE FOR AIMTESTTK.IDOCP_AIM FROM SYSIBM.SYSDUMMY1", nativeQuery = true)
    Long findMaxLineId();

    @Query("SELECT new kr.co.aim.common.dto.powder.IdocOrderMasterResponseDto(" +
            "i.lineId, " +
            "i.idocTypId, " +
            "i.state, " +
            "i.errorCode, " +
            "i.source, " +
            "i.destination, " +
            "i.dtimeCre, " +
            "i.dtimeMod, " +
            "i.usrMod, " +
            "i.pgmMod, " +
            "i.modCnt, " +
            "m.idocId, " +
            "m.cOrderTy, " +
            "m.fromWhCd, " +
            "m.toWhCd " +
            ") " +
            "FROM IdocPEntity i " +
            "JOIN H2OrderMPEntity m ON i.lineId = m.idocId " +
            "WHERE i.idocTypId = :idocTypId") // 1. WHERE 조건 절 추가
    Page<IdocOrderMasterResponseDto> findIdocWithOrderMasterByIdocTypId(
            @Param("idocTypId") Long idocTypId,   // 2. @Param으로 바인딩
            Pageable pageable                    // 3. 페이징 구조 결합
    );

    @Query("SELECT new kr.co.aim.common.dto.powder.IdocH2TransResponseDto(" +
            "i.lineId, " +
            "i.idocTypId, " +
            "i.state, " +
            "i.errorCode, " +
            "i.source, " +
            "i.destination, " +
            "i.dtimeCre, " +
            "i.dtimeMod, " +
            "i.usrMod, " +
            "i.pgmMod, " +
            "i.modCnt, " +
            "d.idocId, " +
            "d.cOrderId, " +
            "d.rrn, " +
            "d.lineNo, " +
            "d.lot, " +
            "d.galKey, " +
            "d.cTransTy, " +
            "d.carrierId, " +
            "d.currRrn, " +
            "d.nextRrn, " +
            "d.actQty, " +
            "d.missQty, " +
            "d.surpQty, " +
            "d.resultStat, " +
            "d.errReason, " +
            "d.eventDt, " +
            "d.h2ordLineId " +
            ") " +
            "FROM IdocPEntity i " +
            "JOIN H2TransPEntity d ON i.lineId = d.idocId " +
            "WHERE d.galKey = :galKey") // 1. WHERE 조건 절 추가
    Page<IdocH2TransResponseDto> findIdocWithH2TransByGalKey(@Param("galKey")String galKey, Pageable pageable);

    @Query("SELECT new kr.co.aim.common.dto.powder.IdocH2PartMResponseDto(" +
            "i.lineId, " +
            "i.idocTypId, " +
            "i.state, " +
            "i.errorCode, " +
            "i.source, " +
            "i.destination, " +
            "i.dtimeCre, " +
            "i.dtimeMod, " +
            "i.usrMod, " +
            "i.pgmMod, " +
            "i.modCnt, " +
            "m.idocId, " +
            "m.cPartId, " +
            "m.cPartDsc, " +
            "m.cPartDsc2, " +
            "m.cratIo, " +
            "m.defaultReceiveQty " +
            ") " +
            "FROM IdocPEntity i " +
            "JOIN H2PartMPEntity m ON i.lineId = m.idocId " +
            "WHERE i.lineId = :idocId") // 1. WHERE 조건 절 추가
    Page<IdocH2PartMResponseDto> findIdocWithPartMasterByIdocId(
            @Param("idocId") Long idocId,   // 2. @Param으로 바인딩
            Pageable pageable                    // 3. 페이징 구조 결합
    );

}