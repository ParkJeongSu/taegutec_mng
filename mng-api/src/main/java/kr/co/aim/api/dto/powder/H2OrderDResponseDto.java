package kr.co.aim.api.dto.powder;


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
    
}