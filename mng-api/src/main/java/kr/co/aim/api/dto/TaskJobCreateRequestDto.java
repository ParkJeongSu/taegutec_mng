package kr.co.aim.api.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED) // JPA Entity 등을 위한 기본 생성자
public class TaskJobCreateRequestDto {

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
}