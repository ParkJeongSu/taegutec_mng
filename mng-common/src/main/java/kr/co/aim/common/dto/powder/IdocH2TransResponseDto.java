package kr.co.aim.common.dto.powder;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class IdocH2TransResponseDto {
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
    private String cOrderId;
    private Integer rrn;
    private Integer lineNo;
    private Integer lot;
    private String galKey;
    private Long cTransTy;
    private String carrierId;
    private Integer currRrn;
    private Integer nextRrn;
    private BigDecimal actQty;
    private BigDecimal missQty;
    private BigDecimal surpQty;
    private String resultStat;
    private String errReason;
    private LocalDateTime eventDt;
    private Long h2ordLineId;

    public IdocH2TransResponseDto(
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
            String cOrderId,
            Integer rrn,
            Integer lineNo,
            Integer lot,
            String galKey,
            Long cTransTy,
            String carrierId,
            Integer currRrn,
            Integer nextRrn,
            BigDecimal actQty,
            BigDecimal missQty,
            BigDecimal surpQty,
            String resultStat,
            String errReason,
            LocalDateTime eventDt,
            Long h2ordLineId

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
        this.cOrderId = cOrderId;
        this.rrn = rrn;
        this.lineNo = lineNo;
        this.lot = lot;
        this.galKey = galKey;
        this.cTransTy = cTransTy;
        this.carrierId = carrierId;
        this.currRrn = currRrn;
        this.nextRrn = nextRrn;
        this.actQty = actQty;
        this.missQty = missQty;
        this.surpQty = surpQty;
        this.resultStat = resultStat;
        this.errReason = errReason;
        this.eventDt = eventDt;
        this.h2ordLineId = h2ordLineId;


    }
}
