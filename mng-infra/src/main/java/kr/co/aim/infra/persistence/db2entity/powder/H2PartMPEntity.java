package kr.co.aim.infra.persistence.db2entity.powder;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "H2PARTMP")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class H2PartMPEntity {

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
    private Long modCnt;

    @Column(name = "CPARTID")
    private String cPartId;

    @Column(name = "CPARTDSC")
    private String cPartDsc;

    @Column(name = "CPARTDSC2")
    private String cPartDsc2;

    @Column(name = "CRATIO")
    private BigDecimal cratIo;

    @Column(name = "DEFAULT_RECEIVE_QTY")
    private BigDecimal defaultReceiveQty;
}