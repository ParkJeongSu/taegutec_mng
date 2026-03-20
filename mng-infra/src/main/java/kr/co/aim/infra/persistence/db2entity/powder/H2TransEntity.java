package kr.co.aim.infra.persistence.db2entity.powder;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@Table(name = "H2TRANS", schema = "TESTDB")
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
    private Long dataCode;

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

    @Column(name = "CGAID")
    private Long cGaId;

    @Column(name = "CGALWHS")
    private String cGalWhs;

    @Column(name = "CCOID")
    private String cCoId;

    @Column(name = "CGRWGACT")
    private Long cGrWgAct;

    @Column(name = "CREQZONE")
    private String cReqZone;

    @Column(name = "CZONE")
    private String cZone;

    @Column(name = "CLOCID")
    private String cLocId;

    @Column(name = "CERRDSC", length = 80)
    private String cErrDsc;

    @Column(name = "CWCID", length = 10)
    private String cWcId;
}