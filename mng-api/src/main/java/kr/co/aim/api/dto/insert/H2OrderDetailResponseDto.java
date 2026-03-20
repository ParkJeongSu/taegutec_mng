package kr.co.aim.api.dto.insert;


import kr.co.aim.api.vo.insert.H2OrderDetailRelocationVo;
import kr.co.aim.api.vo.insert.H2OrderDetailVo;
import kr.co.aim.infra.persistence.db2entity.insert.H2OrderDEntity;
import kr.co.aim.infra.persistence.db2entity.insert.H2OrderMEntity;
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

    public static H2OrderDetailResponseDto from(H2OrderDetailVo vo){
        H2OrderMEntity h2OrderM = vo.getMaster();
        H2OrderDEntity h2OrderD = vo.getDetail();
        return H2OrderDetailResponseDto.builder()
                .lineId(h2OrderM.getIdocId())
                .idocId(h2OrderM.getIdocId())
                .dtimeCre(h2OrderM.getDtimeCre())
                .dtimeMod(h2OrderM.getDtimeMod())
                .usrMod(h2OrderM.getUsrMod())
                .pgmMod(h2OrderM.getPgmMod())
                .modCnt(h2OrderM.getModCnt())
                .dataCode(h2OrderM.getDataCode())
                .bookCtrl(h2OrderM.getBookCtrl())
                .cClient(h2OrderM.getCClient())
                .cOrderId(h2OrderM.getCOrderId())
                .cOrderTy(h2OrderM.getCOrderTy())
                .cDtPick(h2OrderM.getCDtPick())
                .cOrderPrio(h2OrderM.getCOrderPrio())
                .cTCode(h2OrderM.getCTCode())
                .cLocId(h2OrderM.getCLocId())
                .cWcId(h2OrderM.getCWcId())
                .cGalId(h2OrderM.getCGalId())
                .cGalWhs(h2OrderM.getCGalWhs())
                .cHostUsr(h2OrderM.getCHostUsr())
                .cUsrNo(h2OrderM.getCUsrNo())
                .cOrderLn(h2OrderD.getCOrderLn())
                .cCoId(h2OrderD.getCCoId())
                .cCoTy(h2OrderD.getCOrderTy())
                .cZone(h2OrderD.getCZone())
                .cDrivingProfile(h2OrderD.getCDrivingProfile())
                .build();
    }

    public static H2OrderDetailResponseDto form (H2OrderDetailRelocationVo vo){
        H2OrderMEntity h2OrderM = vo.getMaster();
        H2OrderDEntity h2OrderDSource = vo.getSource();
        H2OrderDEntity h2OrderDTarget = vo.getTarget();

        H2OrderDetailResponseDto responseDto =
                H2OrderDetailResponseDto.builder()
                        .lineId(h2OrderM.getIdocId())
                        .idocId(h2OrderM.getIdocId())
                        .dtimeCre(h2OrderM.getDtimeCre())
                        .dtimeMod(h2OrderM.getDtimeMod())
                        .usrMod(h2OrderM.getUsrMod())
                        .pgmMod(h2OrderM.getPgmMod())
                        .modCnt(h2OrderM.getModCnt())
                        .dataCode(h2OrderM.getDataCode())
                        .bookCtrl(h2OrderM.getBookCtrl())
                        .cClient(h2OrderM.getCClient())
                        .cOrderId(h2OrderM.getCOrderId())
                        .cOrderTy(h2OrderM.getCOrderTy())
                        .cDtPick(h2OrderM.getCDtPick())
                        .cOrderPrio(h2OrderM.getCOrderPrio())
                        .cTCode(h2OrderM.getCTCode())
                        .cLocId(h2OrderDTarget.getCZone())
                        .cWcId(h2OrderM.getCWcId())
                        .cGalId(h2OrderM.getCGalId())
                        .cGalWhs(h2OrderDSource.getCZone())
                        .cHostUsr(h2OrderM.getCHostUsr())
                        .cUsrNo(h2OrderM.getCUsrNo())
                        .cOrderLn(h2OrderDSource.getCOrderLn())
                        .cCoId(h2OrderDSource.getCCoId())
                        .cCoTy(h2OrderDSource.getCOrderTy())
                        .cZone(h2OrderDSource.getCZone())
                        .cDrivingProfile(h2OrderDSource.getCDrivingProfile())
                        .build();
        return responseDto;
    }
    
}