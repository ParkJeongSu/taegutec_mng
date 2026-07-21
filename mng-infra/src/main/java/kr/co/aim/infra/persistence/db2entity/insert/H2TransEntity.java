package kr.co.aim.infra.persistence.db2entity.insert;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@Table(name = "H2TRANS")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class H2TransEntity {
    @Id
    @Column(name = "LINEID", nullable = false)
    private Long lineId;

    @Column(name = "IDOCID", nullable = false)
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
    private Integer modCnt;

    @Column(name = "DATACODE")
    private String dataCode;

    @Column(name = "CTRANSTY")
    private Long cTransTy;

    @Column(name = "CCLIENT")
    private String cClient;

    @Column(name = "CORDERID")
    private String cOrderId;

    @Column(name = "CORDERTY")
    private String cOrderTy;

    @Column(name = "CERRID")
    private Long cErrId;

    @Column(name = "CTEXT1")
    private String cText1;

    @Column(name = "CTCODE")
    private String cTCode;

    @Column(name = "CORDERLN")
    private Long cOrderLn;

    @Column(name = "CGALID")
    private String cGalId;

    @Column(name = "CGALWHS")
    private String cGalWhs;

    @Column(name = "CCOID")
    private String cCoId;

    @Column(name = "CGRWGACT")
    private BigDecimal cGrWgAct;

    @Column(name = "CREQZONE")
    private String cReqZone;

    @Column(name = "CZONE")
    private String cZone;

    @Column(name = "CLOCID")
    private String cLocId;

    @Column(name = "CERRDSC")
    private String cErrDsc;

    @Column(name = "CWCID")
    private String cWcId;

    @Column(name = "CGEOCL")
    private String cGeoCl;

    @Column(name = "CUSRID")
    private String cUsrId;

    @Column(name = "PARSEGID")
    private Integer parsegId;

    @Column(name = "PARSEGNAM")
    private String parsegName;
}