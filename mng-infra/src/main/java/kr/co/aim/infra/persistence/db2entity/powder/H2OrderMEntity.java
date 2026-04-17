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
@AllArgsConstructor
@Table(name = "H2ORDERM", catalog = "NEXBEEAS", schema = "dbo")
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

    @Column(name = "USRMOD")
    private String usrMod;

    @Column(name = "PGMMOD")
    private String pgmMod;

    @Column(name = "MODCNT")
    private Integer modCnt;

    @Column(name = "DATACODE")
    private Long dataCode;

    @Column(name = "BOOKCTRL")
    private Long bookCtrl;

    @Column(name = "CCLIENT")
    private String cClient;

    @Column(name = "CORDERID")
    private String cOrderId;

    @Column(name = "CORDERTY")
    private String cOrderTy;

    @Column(name = "CDTPICK")
    private String cDtPick;

    @Column(name = "CORDERPRIO")
    private Integer cOrderPrio;

    @Column(name = "CTCODE")
    private String cTCode;

    @Column(name = "CLOCID")
    private String cLocId;

    @Column(name = "CWCID")
    private String cWcId;

    @Column(name = "CGALID")
    private Long cGalId;

    @Column(name = "CGALWHS")
    private String cGalWhs;

    @Column(name = "CHOSTUSR")
    private String cHostUsr;

    @Column(name = "CUSRNO")
    private String cUsrNo;
}