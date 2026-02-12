package kr.co.aim.api.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED) // JPA Entity 등을 위한 기본 생성자
@Builder
public class PortsResponseDto {

    private Long id;
    private String equipmentName;
    private String portName;
    private String description;
    private String connectedStocker;
    private String transportMode;
    private String portState;
    private String resourceState;
    private String transportState;
    private String carrierName;
    private Long transportJobId;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;

    @QueryProjection
    public PortsResponseDto(
            Long id,
            String equipmentName,
            String portName,
            String description,
            String connectedStocker,
            String transportMode,
            String portState,
            String resourceState,
            String transportState,
            String carrierName,
            Long transportJobId,
            String eventName,
            LocalDateTime eventTime,
            String eventUser,
            String eventComment
    )
    {
        this.id = id;
        this.equipmentName = equipmentName;
        this.portName = portName;
        this.description = description;
        this.connectedStocker = connectedStocker;
        this.transportMode = transportMode;
        this.portState = portState;
        this.resourceState = resourceState;
        this.transportState = transportState;
        this.carrierName = carrierName;
        this.transportJobId = transportJobId;
        this.eventName = eventName;
        this.eventTime = eventTime;
        this.eventUser = eventUser;
        this.eventComment = eventComment;

    }
}