package kr.co.aim.common.condition;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED) // JPA Entity 등을 위한 기본 생성자
public class GALInterfaceSearchCondition {
    private Long lineId;
    private Integer idoctypId;
    private Integer state;
    private Integer errorCode;
    private Integer source;
    private Integer destination;
    private LocalDateTime dtimeCre;
    private LocalDateTime dtimeMod;
    private String usrMod;
    private String pgmMod;
    private Integer modCnt;
}