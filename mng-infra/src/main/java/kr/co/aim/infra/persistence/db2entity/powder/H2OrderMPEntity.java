package kr.co.aim.infra.persistence.db2entity.powder;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "H2ORDERMP")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class H2OrderMPEntity {

    @Id
    @Column(name = "LINEID")
    private Long lineId;

    @Column(name = "IDOCID")
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

    @Column(name = "CORDERTY")
    private String cOrderTy;

    @Column(name = "FROM_WH_CD")
    private String fromWhCd;

    @Column(name = "TO_WH_CD")
    private String toWhCd;
}