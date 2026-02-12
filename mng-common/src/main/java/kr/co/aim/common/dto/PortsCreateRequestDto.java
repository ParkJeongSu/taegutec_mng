package kr.co.aim.common.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED) // JPA Entity 등을 위한 기본 생성자
public class PortsCreateRequestDto {

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