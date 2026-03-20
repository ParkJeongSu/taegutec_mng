package kr.co.aim.api.dto.insert;


import kr.co.aim.infra.persistence.db2entity.insert.H2OrderDEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class H2OrderDResponseDto {

    private Long lineId;

    private Long idocId;

    private LocalDateTime dtimeCre;

    private LocalDateTime dtimeMod;

    private String usrMod;

    private String pgmMod;

    private Integer modCnt;

    private Long dataCode;

    private String cClient;

    private String cOrderId;

    private String cOrderTy;

    private Long cOrderLn;

    private String cCoId;

    private String cCoTy;

    private String cZone;

    private String cDrivingProfile;

    public static H2OrderDResponseDto from(H2OrderDEntity entity) {
        return H2OrderDResponseDto.builder()
                .lineId(entity.getLineId())
                .idocId(entity.getIdocId())
                .dtimeCre(entity.getDtimeCre())
                .dtimeMod(entity.getDtimeMod())
                .usrMod(entity.getUsrMod())
                .pgmMod(entity.getPgmMod())
                .modCnt(entity.getModCnt())
                .dataCode(entity.getDataCode())
                .cClient(entity.getCClient())
                .cOrderId(entity.getCOrderId())
                .cOrderTy(entity.getCOrderTy())
                .cOrderLn(entity.getCOrderLn())
                .cCoId(entity.getCCoId())
                .cCoTy(entity.getCCoTy())
                .cZone(entity.getCZone())
                .cDrivingProfile(entity.getCDrivingProfile())
                .build();
    }
    
}