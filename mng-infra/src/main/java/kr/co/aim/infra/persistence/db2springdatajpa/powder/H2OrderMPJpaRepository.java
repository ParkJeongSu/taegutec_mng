package kr.co.aim.infra.persistence.db2springdatajpa.powder;

import kr.co.aim.common.dto.powder.H2OrderMDetailResponseDto;
import kr.co.aim.infra.persistence.db2entity.powder.H2OrderMPEntity;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@ConditionalOnProperty(name = "factory.type", havingValue = "powder")
public interface H2OrderMPJpaRepository extends JpaRepository<H2OrderMPEntity, Long> {
    Optional<H2OrderMPEntity> findByLineId(Long lineId);
    List<H2OrderMPEntity> findByIdocId(Long idocId);
    Page<H2OrderMPEntity> findByIdocId(Long idocId, Pageable pageable);

    /**
     * 그리드 1에서 선택된 idocId 값을 기준으로 마스터-상세 정보를 조인 조회하는 페이징 메소드
     * @param idocId IDoc 마스터 테이블 연계 고유 키값
     * @param pageable 페이징 및 정렬 오브젝트
     */
    @Query("SELECT new kr.co.aim.common.dto.powder.H2OrderMDetailResponseDto(" +
            "m.lineId, " +
            "m.idocId, " +
            "m.dtimeCre, " +
            "m.dtimeMod, " +
            "m.usrMod, " +
            "m.pgmMod, " +
            "m.modCnt, " +
            "m.cOrderTy, " +
            "m.fromWhCd, " +
            "m.toWhCd, " +
            "d.lineId, " +
            "d.cOrderId, " +
            "d.rrn, " +
            "d.lineNo, " +
            "d.cPartId, " +
            "d.lot, " +
            "d.qty, " +
            "d.uom, " +
            "d.machine, " +
            "d.currRrn, " +
            "d.nextRrn, " +
            "d.minReceiveQty, " +
            "d.maxReceiveQty, " +
            "d.defaultReceiveQty, " +
            "d.h2trnLineId, " +
            "d.galKey" +
            ") " +
            "FROM H2OrderMPEntity m " +
            "JOIN H2OrderDPEntity d ON m.idocId = d.idocId " +
            "WHERE m.idocId = :idocId")
    Page<H2OrderMDetailResponseDto> findH2OrderMDetailByIdocId(
            @Param("idocId") Long idocId,
            Pageable pageable
    );
}
