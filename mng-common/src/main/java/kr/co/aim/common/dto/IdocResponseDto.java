package kr.co.aim.common.dto;


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
    
}