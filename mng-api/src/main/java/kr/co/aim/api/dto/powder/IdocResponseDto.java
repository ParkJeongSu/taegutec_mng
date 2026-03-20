package kr.co.aim.api.dto.powder;


import kr.co.aim.infra.persistence.db2entity.powder.IdocEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class IdocResponseDto {

    private Long lineId; // numeric(10, 0)

    private Long idocTypId; // numeric(10, 0)

    private Integer state; // numeric(10, 0)

    private Integer errorCode; // numeric(10, 0), Default 0

    private Long source; // numeric(10, 0)

    private Long destination; // numeric(10, 0)

    private Long tidId; // numeric(10, 0), Default 0

    private String docNum; // varchar(16)

    private String queueName; // varchar(24)

    private String partnerType; // varchar(2)

    private String partnerName; // varchar(10)

    private String partnerPort; // varchar(10)

    private String msgVariant; // varchar(3)

    private String arcKey; // varchar(70)

    private LocalDateTime dtimeCre; // datetime

    private LocalDateTime dtimeMod; // datetime, Default getdate()

    private String usrMod; // varchar(30)

    private String pgmMod; // varchar(30)

    private Integer modCnt; // numeric(10, 0), Default 0

    public static IdocResponseDto from (IdocEntity entity){
        return IdocResponseDto.builder()
                .lineId(entity.getLineId())
                .idocTypId(entity.getIdocTypId())
                .state(entity.getState())
                .errorCode(entity.getErrorCode())
                .source(entity.getSource())
                .destination(entity.getDestination())
                .tidId(entity.getTidId())
                .docNum(entity.getDocNum())
                .queueName(entity.getQueueName())
                .partnerType(entity.getPartnerType())
                .partnerName(entity.getPartnerName())
                .partnerPort(entity.getPartnerPort())
                .msgVariant(entity.getMsgVariant())
                .arcKey(entity.getArcKey())
                .dtimeCre(entity.getDtimeCre())
                .dtimeMod(entity.getDtimeMod())
                .usrMod(entity.getUsrMod())
                .pgmMod(entity.getPgmMod())
                .modCnt(entity.getModCnt())
                .build();

    }
    
}