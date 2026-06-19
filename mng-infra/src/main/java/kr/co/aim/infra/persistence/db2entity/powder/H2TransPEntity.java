package kr.co.aim.infra.persistence.db2entity.powder;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "H2TRANSP")
@Builder
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class H2TransPEntity {

    @Id
    @Column(name = "LINEID")
    private Long lineId;

    @Column(name = "IDOCID")
    private Long idocId;

    @Column(name = "DTIMECRE")
    private LocalDateTime dtimeCre;

    @Column(name = "DTIMEMOD")
    private LocalDateTime dtimeMod;

    @Column(name = "USRMOD")
    private String usrMod;

    @Column(name = "PGMMOD")
    private String pgmMod;

    @Column(name = "MODCNT")
    private Long modCnt;

    @Column(name = "CORDERID")
    private String cOrderId;

    @Column(name = "RRN")
    private Integer rrn;

    @Column(name = "LINENO")
    private Integer lineNo;

    @Column(name = "LOT")
    private Integer lot;

    @Column(name = "GALKEY")
    private String galKey;

    @Column(name = "CTRANSTY")
    private Long cTransTy;

    @Column(name = "CARRIER_ID")
    private String carrierId;

    @Column(name = "CURR_RRN")
    private Integer currRrn;

    @Column(name = "NEXT_RRN")
    private Integer nextRrn;

    @Column(name = "ACT_QTY")
    private BigDecimal actQty;

    @Column(name = "MISS_QTY")
    private BigDecimal missQty;

    @Column(name = "SURP_QTY")
    private BigDecimal surpQty;

    @Column(name = "RESULT_STAT")
    private String resultStat;

    @Column(name = "ERR_REASON")
    private String errReason;

    @Column(name = "EVENT_DT")
    private LocalDateTime eventDt;

//    @Column(name = "H2ORD_LINEID")
//    private Long h2ordLineId;

    @Column(name = "CPARTID")
    private String cPartId;

//    @Column(name = "REF_LINEID")
//    private Long refLineId;

    @Column(name = "MNGKEY")
    private Long mngKey;
}