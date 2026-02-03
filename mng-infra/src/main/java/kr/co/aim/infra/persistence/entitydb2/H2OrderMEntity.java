package kr.co.aim.infra.persistence.entitydb2;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Table(name = "H2ORDERM", schema = "TESTDB")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class H2OrderMEntity {
    @Id
    @Column(name = "LINEID", nullable = false)
    private Long lineId;

    @Column(name = "IDOCID", nullable = false)
    private Long idocId;

    @Column(name = "DTIMECRE")
    private LocalDateTime dtimeCre;

    @Column(name = "DTIMEMOD")
    private LocalDateTime dtimeMod;

    @Column(name = "USRMOD", length = 30)
    private String usrMod;

    @Column(name = "PGMMOD", length = 30)
    private String pgmMod;

    @Column(name = "MODCNT")
    private Integer modCnt;

    @Column(name = "DATACODE")
    private Long dataCode;

    @Column(name = "BOOKCTRL")
    private Long bookCtrl;

    @Column(name = "CCLIENT", length = 30)
    private String cClient;

    @Column(name = "CORDERID", length = 30)
    private String cOrderId;

    @Column(name = "CORDERTY", length = 30)
    private String cOrderTy;

    @Column(name = "CDTPICK", length = 30)
    private String cDtPick;

    @Column(name = "CORDERPRIO")
    private Integer cOrderPrio;

    @Column(name = "CTCODE", length = 30)
    private String cTCode;

    @Column(name = "CLOCID", length = 30)
    private String cLocId;

    @Column(name = "CWCID", length = 30)
    private String cWcId;

    @Column(name = "CGALID")
    private Long cGalId;

    @Column(name = "CGALWHS", length = 30)
    private String cGalWhs;

    @Column(name = "CHOSTUSR", length = 30)
    private String cHostUsr;

    @Column(name = "CUSRNO", length = 30)
    private String cUsrNo;
}