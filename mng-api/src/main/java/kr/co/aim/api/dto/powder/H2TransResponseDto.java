package kr.co.aim.api.dto.powder;


import kr.co.aim.infra.persistence.db2entity.powder.H2TransEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class H2TransResponseDto {


    private Long lineId;

    private Long idocId;

    private LocalDateTime dtimeCre;

    private LocalDateTime dtimeMod;

    private String usrMod;

    private String pgmMod;

    private Integer modCnt;

    private Long dataCode;

    private Long cTransTy;

    private String cClient;

    private String cOrderId;

    private String cOrderTy;

    private Long cErrId;

    private String cText1;

    private String cTCode;

    private Long cOrderLn;

    private Long cGaId;

    private String cGalWhs;

    private String cCoId;

    private Long cGrWgAct;

    private String cReqZone;

    private String cZone;

    private String cLocId;

    private String cErrDsc;

    private String cWcId;

    public static H2TransResponseDto from (H2TransEntity entity){
        return H2TransResponseDto.builder()
                .lineId(entity.getLineId())
                .idocId(entity.getIdocId())
                .dtimeCre(entity.getDtimeCre())
                .dtimeMod(entity.getDtimeMod())
                .usrMod(entity.getUsrMod())
                .pgmMod(entity.getPgmMod())
                .modCnt(entity.getModCnt())
                .dataCode(entity.getDataCode())
                .cTransTy(entity.getCTransTy())
                .cClient(entity.getCClient())
                .cOrderId(entity.getCOrderId())
                .cOrderTy(entity.getCOrderTy())
                .cErrId(entity.getCErrId())
                .cText1(entity.getCText1())
                .cTCode(entity.getCTCode())
                .cOrderLn(entity.getCOrderLn())
                .cGaId(entity.getCGaId())
                .cGalWhs(entity.getCGalWhs())
                .cCoId(entity.getCCoId())
                .cGrWgAct(entity.getCGrWgAct())
                .cReqZone(entity.getCReqZone())
                .cZone(entity.getCZone())
                .cLocId(entity.getCLocId())
                .cErrDsc(entity.getCErrDsc())
                .cWcId(entity.getCWcId())
                .build();

    }
    
}