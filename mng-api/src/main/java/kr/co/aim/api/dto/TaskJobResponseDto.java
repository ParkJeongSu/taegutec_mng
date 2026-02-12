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
public class TaskJobResponseDto {

    private Long id;
    private String taskName;
    private String taskType;
    private String equipmentName;
    private String taskGroupName;
    private Integer step;
    private Long workOrderId;
    private String taskState;
    private Integer carrierCount;
    private Integer transportTryCount;
    private String recipeName;
    private LocalDateTime createTime;
    private LocalDateTime departedTime;
    private LocalDateTime arrivedTime;
    private LocalDateTime startTime;
    private LocalDateTime completedTime;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;

    @QueryProjection // ✨ 이 어노테이션이 있어야 QUserResponseDto가 생성됩니다.
    public TaskJobResponseDto(
            Long id,
            String taskName,
            String taskType,
            String equipmentName,
            String taskGroupName,
            Integer step,
            Long workOrderId,
            String taskState,
            Integer carrierCount,
            Integer transportTryCount,
            String recipeName,
            LocalDateTime createTime,
            LocalDateTime departedTime,
            LocalDateTime arrivedTime,
            LocalDateTime startTime,
            LocalDateTime completedTime,
            String eventName,
            LocalDateTime eventTime,
            String eventUser,
            String eventComment
    ){
        this.id = id;
        this.taskName = taskName;
        this.taskType = taskType;
        this.equipmentName = equipmentName;
        this.taskGroupName = taskGroupName;
        this.step = step;
        this.workOrderId = workOrderId;
        this.taskState = taskState;
        this.carrierCount = carrierCount;
        this.transportTryCount = transportTryCount;
        this.recipeName = recipeName;
        this.createTime = createTime;
        this.departedTime = departedTime;
        this.arrivedTime = arrivedTime;
        this.startTime = startTime;
        this.completedTime = completedTime;
        this.eventName = eventName;
        this.eventTime = eventTime;
        this.eventUser = eventUser;
        this.eventComment = eventComment;
    }
}