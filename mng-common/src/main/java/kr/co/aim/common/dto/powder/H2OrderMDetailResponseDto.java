package kr.co.aim.common.dto.powder;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
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
    private String galKey;

}