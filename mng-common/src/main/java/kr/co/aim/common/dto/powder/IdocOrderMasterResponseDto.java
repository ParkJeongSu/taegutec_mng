package kr.co.aim.common.dto.powder;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IdocOrderMasterResponseDto {
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
    private String cOrderTy;
    private String fromWhCd;
    private String toWhCd;

}
