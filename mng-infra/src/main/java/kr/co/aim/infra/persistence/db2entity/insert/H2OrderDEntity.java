package kr.co.aim.infra.persistence.db2entity.insert;

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
@Table(name = "H2ORDERD")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class H2OrderDEntity {
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

    @Column(name = "CCLIENT")
    private String cClient;

    @Column(name = "CORDERID")
    private String cOrderId;

    @Column(name = "CORDERTY")
    private String cOrderTy;

    @Column(name = "CORDERLN")
    private Long cOrderLn;

    @Column(name = "CCOID")
    private String cCoId;

    @Column(name = "CCOTY")
    private String cCoTy;

    @Column(name = "CZONE")
    private String cZone;

    @Column(name = "CDRIVINGPROFILE")
//    @jakarta.persistence.Transient
    private String cDrivingProfile;

    @Column(name = "PARSEGID")
    private Integer parsegId;

    @Column(name = "PARSEGNAM")
    private String parsegName;
}