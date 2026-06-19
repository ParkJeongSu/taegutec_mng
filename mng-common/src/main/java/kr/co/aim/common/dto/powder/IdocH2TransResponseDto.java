package kr.co.aim.common.dto.powder;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IdocH2TransResponseDto {
    private Long lineId;
    private Long idocTypId;
    private Long state;
    private Long errorCode;
    private Long source;
    private Long destination;
    private LocalDateTime dtimeCre;
    private LocalDateTime dtimeMod;
    private String usrMod;
    private String pgmMod;
    private Long modCnt;
    private Long idocId;
    private String cOrderId;
    private Integer rrn;
    private Integer lineNo;
    private Integer lot;
    private String galKey;
    private Long cTransTy;
    private String carrierId;
    private Integer currRrn;
    private Integer nextRrn;
    private BigDecimal actQty;
    private BigDecimal missQty;
    private BigDecimal surpQty;
    private String resultStat;
    private String errReason;
    private LocalDateTime eventDt;
    private String cPartId;
    private Long mngKey;

}
