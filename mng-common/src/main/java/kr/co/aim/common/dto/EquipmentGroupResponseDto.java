package kr.co.aim.common.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED) // JPA Entity 등을 위한 기본 생성자
@Builder
public class EquipmentGroupResponseDto {

    private Long id;
    private String equipmentGroupName;
    private String description;
    private String checkOutState;
    private LocalDateTime checkOutTime;
    private String checkOutUser;
    private String dataState;
    private String eventName;
    
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;

    @QueryProjection
    public EquipmentGroupResponseDto(
            Long id,
            String equipmentGroupName,
            String description,
            String checkOutState,
            LocalDateTime checkOutTime,
            String checkOutUser,
            String dataState,
            String eventName,
            
            LocalDateTime eventTime,
            String eventUser,
            String eventComment
    ){
        this.id = id;
        this.equipmentGroupName = equipmentGroupName;
        this.description = description;
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