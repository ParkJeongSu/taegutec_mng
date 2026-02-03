package kr.co.aim.infra.persistence.entitydb2;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Table(name = "H2ORDERD", schema = "TESTDB")
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

    @Column(name = "USRMOD", length = 30)
    private String usrMod;

    @Column(name = "PGMMOD", length = 30)
    private String pgmMod;

    @Column(name = "MODCNT")
    private Integer modCnt;

    @Column(name = "DATACODE")
    private Long dataCode;

    @Column(name = "CCLIENT", length = 30)
    private String cClient;

    @Column(name = "CORDERID", length = 30)
    private String cOrderId;

    @Column(name = "CORDERTY", length = 30)
    private String cOrderTy;

    @Column(name = "CORDERLN")
    private Long cOrderLn;

    @Column(name = "CCOID", length = 30)
    private String cCoId;

    @Column(name = "CCOTY", length = 30)
    private String cCoTy;

    @Column(name = "CZONE", length = 30)
    private String cZone;

    @Column(name = "CDRIVINGPROFILE", length = 30)
    private String cDrivingProfile;
}