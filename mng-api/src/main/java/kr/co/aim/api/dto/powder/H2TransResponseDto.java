package kr.co.aim.api.dto.powder;


import kr.co.aim.infra.persistence.db2entity.powder.H2TransPEntity;
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
    
}