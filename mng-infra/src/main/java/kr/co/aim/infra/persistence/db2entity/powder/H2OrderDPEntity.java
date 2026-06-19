package kr.co.aim.infra.persistence.db2entity.powder;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "H2ORDERDP")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class H2OrderDPEntity {

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

    @Column(name = "CPARTID")
    private String cPartId;

    @Column(name = "LOT")
    private Integer lot;

    @Column(name = "QTY")
    private BigDecimal qty;

    @Column(name = "UOM")
    private String uom;

    @Column(name = "MACHINE")
    private String machine;

    @Column(name = "CURR_RRN")
    private Integer currRrn;

    @Column(name = "NEXT_RRN")
    private Integer nextRrn;

//    @Column(name = "MIN_RECEIVE_QTY")
//    private BigDecimal minReceiveQty;
//
//    @Column(name = "MAX_RECEIVE_QTY")
//    private BigDecimal maxReceiveQty;
//
//    @Column(name = "DEFAULT_RECEIVE_QTY")
//    private BigDecimal defaultReceiveQty;
//
//    @Column(name = "H2TRN_LINEID")
//    private Long h2trnLineId;

    @Column(name = "GALKEY")
    private String galKey;

    @Column(name = "REF_LOT")
    private Integer refLot;

    @Column(name = "CMOORD")
    private Integer cmoord;

    @Column(name = "MNGKEY")
    private Long mngKey;
}