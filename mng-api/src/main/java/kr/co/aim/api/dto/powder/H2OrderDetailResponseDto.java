package kr.co.aim.api.dto.powder;


import kr.co.aim.api.vo.powder.sim.H2OrderDetailRelocationVo;
import kr.co.aim.api.vo.powder.sim.H2OrderDetailVo;
import kr.co.aim.infra.persistence.db2entity.powder.H2OrderDPEntity;
import kr.co.aim.infra.persistence.db2entity.powder.H2OrderMPEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class H2OrderDetailResponseDto {

    private Long lineId;
    private Long idocId;
    private LocalDateTime dtimeCre;
    private LocalDateTime dtimeMod;
    private String usrMod;
    private String pgmMod;
    private Integer modCnt;
    private Long dataCode;
    private Long bookCtrl;
    private String cClient;
    private String cOrderId;
    private String cOrderTy;
    private String cDtPick;
    private Integer cOrderPrio;
    private String cTCode;
    private String cLocId;
    private String cWcId;
    private Long cGalId;
    private String cGalWhs;
    private String cHostUsr;
    private String cUsrNo;
    private Long cOrderLn;
    private String cCoId;
    private String cCoTy;
    private String cZone;
    private String cDrivingProfile;
}