package kr.co.aim.common.dto;

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
public class TaskJobDetailResponseDto {

    private Long id;
    private Long taskJobId;
    private String wipName;
    private String carrierName;
    private String state;
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
    public TaskJobDetailResponseDto(
            Long id,
            Long taskJobId,
            String wipName,
            String carrierName,
            String state,
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
        this.taskJobId = taskJobId;
        this.wipName = wipName;
        this.carrierName = carrierName;
        this.state = state;
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