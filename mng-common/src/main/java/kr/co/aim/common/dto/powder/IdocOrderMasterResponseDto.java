package kr.co.aim.common.dto.powder;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class IdocOrderMasterResponseDto {
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
    private String cOrderTy;
    private String fromWhCd;
    private String toWhCd;

    public IdocOrderMasterResponseDto(
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
            String cOrderTy,
            String fromWhCd,
            String toWhCd
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
        this.cOrderTy = cOrderTy;
        this.fromWhCd = fromWhCd;
        this.toWhCd = toWhCd;
    }
}
