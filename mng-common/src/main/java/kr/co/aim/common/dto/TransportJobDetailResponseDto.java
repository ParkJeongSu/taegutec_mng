package kr.co.aim.common.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED) // JPA Entity 등을 위한 기본 생성자
@Builder
public class TransportJobDetailResponseDto {
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
    private Integer stepOrder;
    private Integer stepPhase;
    private String eventName;
    
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;

    @QueryProjection // ✨ 이 어노테이션이 있어야 QUserResponseDto가 생성됩니다.
    public TransportJobDetailResponseDto(
            Long id,
            String transportJobDetailName,
            Long transportJobId,
            String transportJobDetailState,
            String carrierId,
            String sourceEquipmentName,
            String sourcePortName,
            String sourceZoneName,
            String sourceShelfName,
            String destinationEquipmentName,
            String destinationPortName,
            String destinationZoneName,
            String destinationShelfName,
            String currentEquipmentName,
            String currentPortName,
            String currentZoneName,
            String currentShelfName,
            Integer stepOrder,
            Integer stepPhase,
            String eventName,
            
            LocalDateTime eventTime,
            String eventUser,
            String eventComment
    ){
        this.id = id;
        this.transportJobDetailName = transportJobDetailName;
        this.transportJobId = transportJobId;
        this.transportJobDetailState = transportJobDetailState;
        this.carrierId = carrierId;
        this.sourceEquipmentName = sourceEquipmentName;
        this.sourcePortName = sourcePortName;
        this.sourceZoneName = sourceZoneName;
        this.sourceShelfName = sourceShelfName;
        this.destinationEquipmentName = destinationEquipmentName;
        this.destinationPortName = destinationPortName;
        this.destinationZoneName = destinationZoneName;
        this.destinationShelfName = destinationShelfName;
        this.currentEquipmentName = currentEquipmentName;
        this.currentPortName = currentPortName;
        this.currentZoneName = currentZoneName;
        this.currentShelfName = currentShelfName;
        this.stepOrder = stepOrder;
        this.stepPhase = stepPhase;
        this.eventName = eventName;
        
        this.eventTime = eventTime;
        this.eventUser = eventUser;
        this.eventComment = eventComment;
    }
}