package kr.co.aim.infra.persistence.db2entity.powder;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "IDOCP")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IdocPEntity {

    @Id
    @Column(name = "LINEID")
    private Long lineId;

    @Column(name = "IDOCTYPID")
    private Integer idocTypId;

    @Column(name = "STATE")
    private Integer state;

    @Column(name = "ERRORCODE")
    private Integer errorCode;

    @Column(name = "SOURCE")
    private Integer source;

    @Column(name = "DESTINATION")
    private Integer destination;

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
