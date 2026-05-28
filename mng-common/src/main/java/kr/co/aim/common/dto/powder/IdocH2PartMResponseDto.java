package kr.co.aim.common.dto.powder;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class IdocH2PartMResponseDto {
    private Long lineId;
    private Long idocTypId;
    private Long state;
    private Long errorCode;
    private Long source;
    private Long destination;
    private LocalDateTime dtimeCre;
    private LocalDateTime dtimeMod;
    private String usrMod;
    private String pgmMod;
    private Long modCnt;
    private Long idocId;
    private String cPartId;
    private String cPartDsc;
    private String cPartDsc2;
    private BigDecimal cratIo;
    private BigDecimal defaultReceiveQty;

    public IdocH2PartMResponseDto(
            Long lineId,
            Long idocTypId,
            Long state,
            Long errorCode,
            Long source,
            Long destination,
            LocalDateTime dtimeCre,
            LocalDateTime dtimeMod,
            String usrMod,
            String pgmMod,
            Long modCnt,
            Long idocId,
            String cPartId,
            String cPartDsc,
            String cPartDsc2,
            BigDecimal cratIo,
            BigDecimal defaultReceiveQty
    ) {
        this.lineId = lineId;
        this.idocTypId = idocTypId;
        this.state = state;
        this.errorCode = errorCode;
        this.source = source;
        this.destination = destination;
        this.dtimeCre = dtimeCre;
        this.dtimeMod = dtimeMod;
        this.usrMod = usrMod;
        this.pgmMod = pgmMod;
        this.modCnt = modCnt;
        this.idocId = idocId;
        this.cPartId = cPartId;
        this.cPartDsc = cPartDsc;
        this.cPartDsc2 = cPartDsc2;
        this.cratIo = cratIo;
        this.defaultReceiveQty = defaultReceiveQty;
    }
}
