package kr.co.aim.common.condition;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED) // JPA Entity 등을 위한 기본 생성자
public class GALPartSearchCondition {
    private Long lineId;
    private Long idocId;
    private LocalDateTime dtimeCre;
    private LocalDateTime dtimeMod;
    private String usrMod;
    private String pgmMod;
    private Integer modCnt;
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
    private Long h2trnLineid;
    private String galKey;
}