package kr.co.aim.common.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED) // JPA Entity 등을 위한 기본 생성자
public class PortsUpdateRequestDto {

    private Long id;
    private String equipmentName;
    private String portName;
    private Long portDefId;
    private String description;
    private String connectedStocker;
    private String transportMode;
    private String portState;
    private String resourceState;
    private String transportState;
    private String carrierName;
    private String eventName;
    
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;
}