package kr.co.aim.common.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED) // JPA Entity 등을 위한 기본 생성자
@Builder
public class TransportJobResponseDto {
    private Long id;
    private String transportJobName;
    private String transportJobState;
    private String sourceEquipmentName;
    private String sourcePortName;
    private String sourceZoneName;
    private String sourceShelfName;
    private String destinationEquipmentName;
    private String destinationPortName;
    private String destinationZoneName;
    private String destinationShelfName;
    private Integer priority;
    private String errorCode;
    private String errorText;
    private String requestType;
    private LocalDateTime createTime;
    private String reasonCode;
    private String eventName;
    
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;

    @QueryProjection // ✨ 이 어노테이션이 있어야 QUserResponseDto가 생성됩니다.
    public TransportJobResponseDto(
            Long id,
            String transportJobName,
            String transportJobState,
            String sourceEquipmentName,
            String sourcePortName,
            String sourceZoneName,
            String sourceShelfName,
            String destinationEquipmentName,
            String destinationPortName,
            String destinationZoneName,
            String destinationShelfName,
            Integer priority,
            String errorCode,
            String errorText,
            String requestType,
            LocalDateTime createTime,
            String reasonCode,
            String eventName,
            
            LocalDateTime eventTime,
            String eventUser,
            String eventComment
    ){
        this.id = id;
        this.transportJobName = transportJobName;
        this.transportJobState = transportJobState;
        this.sourceEquipmentName = sourceEquipmentName;
        this.sourcePortName = sourcePortName;
        this.sourceZoneName = sourceZoneName;
        this.sourceShelfName = sourceShelfName;
        this.destinationEquipmentName = destinationEquipmentName;
        this.destinationPortName = destinationPortName;
        this.destinationZoneName = destinationZoneName;
        this.destinationShelfName = destinationShelfName;
        this.priority = priority;
        this.errorCode = errorCode;
        this.errorText = errorText;
        this.requestType = requestType;
        this.createTime = createTime;
        this.reasonCode = reasonCode;
        this.eventName = eventName;
        
        this.eventTime = eventTime;
        this.eventUser = eventUser;
        this.eventComment = eventComment;
    }
}