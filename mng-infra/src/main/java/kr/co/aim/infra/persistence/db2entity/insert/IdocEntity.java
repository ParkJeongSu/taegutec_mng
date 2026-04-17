package kr.co.aim.infra.persistence.db2entity.insert;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.co.aim.common.enums.IdocMachine;
import kr.co.aim.common.enums.IdocTypeId;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Builder
@Table(name = "IDOC", catalog = "NEXBEEAS", schema = "dbo")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IdocEntity {
    @Id
    @Column(name = "LINEID", nullable = false)
    private Long lineId;

    @Column(name = "IDOCTYPID")
    private Long idocTypId;

    @Column(name = "STATE")
    private Integer state;

    @Column(name = "ERRORCODE")
    private Integer errorCode;

    @Column(name = "SOURCE")
    private Long source;

    @Column(name = "DESTINATION")
    private Long destination;

    @Column(name = "TIDID")
    private Long tidId;

    @Column(name = "DOCNUM")
    private String docNum;

    @Column(name = "QUEUENAME")
    private String queueName;

    @Column(name = "PARTNERTYPE")
    private String partnerType;

    @Column(name = "PARTNERNAME")
    private String partnerName;

    @Column(name = "PARTNERPORT")
    private String partnerPort;

    @Column(name = "MSGVARIANT")
    private String msgVariant;

    @Column(name = "ARCKEY")
    private String arcKey;

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

}
