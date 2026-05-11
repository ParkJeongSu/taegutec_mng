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
    private Long lineid;
    private Long idocid;
    private LocalDateTime dtimecre;
    private LocalDateTime dtimemod;
    private String usrmod;
    private String pgmmod;
    private Integer modcnt;
    private String corderid;
    private Integer rrn;
    private Integer lineno;
    private String cpartid;
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
    private String galkey;
}