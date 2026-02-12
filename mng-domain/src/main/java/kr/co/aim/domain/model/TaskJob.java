package kr.co.aim.domain.model;

import kr.co.aim.common.handler.HasTransactionInfo;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class TaskJob implements HasTransactionInfo {
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
