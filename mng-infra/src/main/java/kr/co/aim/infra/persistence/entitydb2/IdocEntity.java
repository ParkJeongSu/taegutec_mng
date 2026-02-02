package kr.co.aim.infra.persistence.entitydb2;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Table(name = "IDOC", schema = "TESTDB")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA를 위한 기본 생성자
public class IdocEntity {
    @Id
    @Column(name = "LINEID", nullable = false)
    private Long lineId; // numeric(10, 0)

    @Column(name = "IDOCTYPID")
    private Long idocTypId; // numeric(10, 0)

    @Column(name = "STATE")
    private Integer state; // numeric(10, 0), Default -1

    @Column(name = "ERRORCODE")
    private Integer errorCode; // numeric(10, 0), Default 0

    @Column(name = "SOURCE")
    private Long source; // numeric(10, 0)

    @Column(name = "DESTINATION")
    private Long destination; // numeric(10, 0)

    @Column(name = "TIDID")
    private Long tidId; // numeric(10, 0), Default 0

    @Column(name = "DOCNUM", length = 16)
    private String docNum; // varchar(16)

    @Column(name = "QUEUENAME", length = 24)
    private String queueName; // varchar(24)

    @Column(name = "PARTNERTYPE", length = 2)
    private String partnerType; // varchar(2)

    @Column(name = "PARTNERNAME", length = 10)
    private String partnerName; // varchar(10)

    @Column(name = "PARTNERPORT", length = 10)
    private String partnerPort; // varchar(10)

    @Column(name = "MSGVARIANT", length = 3)
    private String msgVariant; // varchar(3)

    @Column(name = "ARCKEY", length = 70)
    private String arcKey; // varchar(70)

    @Column(name = "DTIMECRE")
    private LocalDateTime dtimeCre; // datetime

    @Column(name = "DTIMEMOD")
    private LocalDateTime dtimeMod; // datetime, Default getdate()

    @Column(name = "USRMOD", length = 30)
    private String usrMod; // varchar(30)

    @Column(name = "PGMMOD", length = 30)
    private String pgmMod; // varchar(30)

    @Column(name = "MODCNT")
    private Integer modCnt; // numeric(10, 0), Default 0

}
