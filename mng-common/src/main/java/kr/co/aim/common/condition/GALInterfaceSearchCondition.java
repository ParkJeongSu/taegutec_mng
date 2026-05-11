package kr.co.aim.common.condition;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED) // JPA Entity 등을 위한 기본 생성자
public class GALInterfaceSearchCondition {
    private Long lineid;
    private Integer idoctypid;
    private Integer state;
    private Integer errorcode;
    private Integer source;
    private Integer destination;
    private LocalDateTime dtimecre;
    private LocalDateTime dtimemod;
    private String usrmod;
    private String pgmmod;
    private Integer modcnt;
}