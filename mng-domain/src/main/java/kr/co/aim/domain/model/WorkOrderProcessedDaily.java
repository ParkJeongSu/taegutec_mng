package kr.co.aim.domain.model;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class WorkOrderProcessedDaily {

    private IdWorkOrderProcessedDaily id;
    private Integer totalProcessedCount;
    private Integer avgProcessedTime;
    private Integer totalQuantity;

}