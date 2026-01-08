package kr.co.aim.common.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED) // JPA Entity 등을 위한 기본 생성자
public class PortDefUpdateRequestDto {

    private Long id;
    private String portDefName;
    private String description;
    private String portType;
    private String portUseType;
    private Long useCarrierDefId;
    private String checkOutState;
    private LocalDateTime checkOutTime;
    private String checkOutUser;
    private String dataState;
    private String eventName;

    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;
}