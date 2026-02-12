package kr.co.aim.api.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED) // JPA Entity 등을 위한 기본 생성자
public class TransportJobDetailCreateRequestDto {
    private Long id;
    private String transportJobDetailName;
    private Long transportJobId;
    private String transportJobDetailState;
    private String carrierId;
    private String sourceEquipmentName;
    private String sourcePortName;
    private String sourceZoneName;
    private String sourceShelfName;
    private String destinationEquipmentName;
    private String destinationPortName;
    private String destinationZoneName;
    private String destinationShelfName;
    private String currentEquipmentName;
    private String currentPortName;
    private String currentZoneName;
    private String currentShelfName;
    private Integer order;
    private Integer jobPhase;
    private String eventName;
    
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;
}