package kr.co.aim.infra.persistence.db2entity.powder;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "IDOCP")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IdocPEntity {

    @Id
    @Column(name = "LINEID")
    private Long lineId;

    @Column(name = "IDOCTYPID")
    private Long idocTypId;

    @Column(name = "STATE")
    private Long state;

    @Column(name = "ERRORCODE")
    private Long errorCode;

    @Column(name = "SOURCE")
    private Long source;

    @Column(name = "DESTINATION")
    private Long destination;

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
}
