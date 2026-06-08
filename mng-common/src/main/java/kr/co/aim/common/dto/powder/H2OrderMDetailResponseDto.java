package kr.co.aim.common.dto.powder;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class H2OrderMDetailResponseDto {
    // 1. H2ORDERMP (오더 마스터) 필드
    private Long masterLineId;
    private Long idocId;
    private LocalDateTime masterDtimeCre;
    private LocalDateTime masterDtimeMod;
    private String masterUsrMod;
    private String masterPgmMod;
    private Long masterModCnt;
    private String corderTy;
    private String fromWhCd;
    private String toWhCd;

    // 2. H2ORDERDP (오더 상세) 필드
    private Long lineId; // 상세 테이블의 PK (그리드 Row Key로 사용)
    private String cOrderId;
    private Integer rrn;
    private Integer lineNo;
    private String cPartId;
    private Integer lot;
    private BigDecimal qty;
    private String uom;
    private String machine;
    private Integer currRrn;
    private Integer nextRrn;
    private BigDecimal minReceiveQty;
    private BigDecimal maxReceiveQty;
    private BigDecimal defaultReceiveQty;
    private Long h2trnLineId;
    private String galKey;

    // JPQL new 인스턴스 생성을 위한 매핑 생성자
    public H2OrderMDetailResponseDto(
            Long masterLineId, Long idocId, LocalDateTime masterDtimeCre, LocalDateTime masterDtimeMod,
            String masterUsrMod, String masterPgmMod, Long masterModCnt, String corderTy, String fromWhCd, String toWhCd,
            Long lineId, String cOrderId, Integer rrn, Integer lineNo, String cPartId, Integer lot,
            BigDecimal qty, String uom, String machine, Integer currRrn, Integer nextRrn,
            BigDecimal minReceiveQty, BigDecimal maxReceiveQty, BigDecimal defaultReceiveQty,
            Long h2trnLineId, String galKey
    ) {
        this.masterLineId = masterLineId;
        this.idocId = idocId;
        this.masterDtimeCre = masterDtimeCre;
        this.masterDtimeMod = masterDtimeMod;
        this.masterUsrMod = masterUsrMod;
        this.masterPgmMod = masterPgmMod;
        this.masterModCnt = masterModCnt;
        this.corderTy = corderTy;
        this.fromWhCd = fromWhCd;
        this.toWhCd = toWhCd;

        this.lineId = lineId;
        this.cOrderId = cOrderId;
        this.rrn = rrn;
        this.lineNo = lineNo;
        this.cPartId = cPartId;
        this.lot = lot;
        this.qty = qty;
        this.uom = uom;
        this.machine = machine;
        this.currRrn = currRrn;
        this.nextRrn = nextRrn;
        this.minReceiveQty = minReceiveQty;
        this.maxReceiveQty = maxReceiveQty;
        this.defaultReceiveQty = defaultReceiveQty;
        this.h2trnLineId = h2trnLineId;
        this.galKey = galKey;
    }
}