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
public class PortDefResponseDto {

    private Long id;
    private String equipmentName;
    private String portName;
    private String description;
    private String portType;
    private String portUseType;
    private String containerType;
    private String checkOutState;
    private LocalDateTime checkOutTime;
    private String checkOutUser;
    private String dataState;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;

    @QueryProjection
    public PortDefResponseDto
            (
                    Long id,
                    String equipmentName,
                    String portName,
                    String description,
                    String portType,
                    String portUseType,
                    String containerType,
                    String checkOutState,
                    LocalDateTime checkOutTime,
                    String checkOutUser,
                    String dataState,
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
        this.portType = portType;
        this.portUseType = portUseType;
        this.containerType = containerType;
        this.checkOutState = checkOutState;
        this.checkOutTime = checkOutTime;
        this.checkOutUser = checkOutUser;
        this.dataState = dataState;
        this.eventName = eventName;
        this.eventTime = eventTime;
        this.eventUser = eventUser;
        this.eventComment = eventComment;
    }
}