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
public class TaskJobDetail implements HasTransactionInfo {
    private Long id;
    private Long taskJobId;
    private String wipName;
    // TODO: sourceEquipmentName , destinationEquipmentName 추가
    // WMS로 부터 현재 carrier 의 위치와 최종 목적지의 설비명을 저장 후 WCS로 반송요청
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

}
