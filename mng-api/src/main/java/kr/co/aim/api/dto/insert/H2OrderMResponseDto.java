package kr.co.aim.api.dto.insert;


import kr.co.aim.infra.persistence.db2entity.insert.H2OrderMEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class H2OrderMResponseDto {

    private Long lineId;

    private Long idocId;

    private LocalDateTime dtimeCre;

    private LocalDateTime dtimeMod;

    private String usrMod;

    private String pgmMod;

    private Integer modCnt;

    private String dataCode;

    private String bookCtrl;

    private String cClient;

    private String cOrderId;

    private String cOrderTy;

    private String cDtPick;

    private Integer cOrderPrio;

    private String cTCode;

    private String cLocId;

    private String cWcId;

    private String cGalId;

    private String cGalWhs;

    private String cHostUsr;

    private String cUsrNo;


    public static H2OrderMResponseDto from(H2OrderMEntity entity) {
        return H2OrderMResponseDto.builder()
                .lineId(entity.getLineId())
                .idocId(entity.getIdocId())
                .dtimeCre(entity.getDtimeCre())
                .dtimeMod(entity.getDtimeMod())
                .usrMod(entity.getUsrMod())
                .pgmMod(entity.getPgmMod())
                .modCnt(entity.getModCnt())
                .dataCode(entity.getDataCode())
                .bookCtrl(entity.getBookCtrl())
                .cClient(entity.getCClient())
                .cOrderId(entity.getCOrderId())
                .cOrderTy(entity.getCOrderTy())
                .cDtPick(entity.getCDtPick())
                .cOrderPrio(entity.getCOrderPrio())
                .cTCode(entity.getCTCode())
                .cLocId(entity.getCLocId())
                .cWcId(entity.getCWcId())
                .cGalId(entity.getCGalId())
                .cGalWhs(entity.getCGalWhs())
                .cHostUsr(entity.getCHostUsr())
                .cUsrNo(entity.getCUsrNo())
                .build();
    }
}