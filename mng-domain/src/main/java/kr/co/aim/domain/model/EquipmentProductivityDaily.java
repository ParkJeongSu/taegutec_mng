package kr.co.aim.domain.model;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class EquipmentProductivityDaily {


    private IdProductivityDaily id;
    private Integer totalProcessedCount;
    private Integer totalProcessedQuantity;
    private Integer okProcessed;
    private Integer ngProcessed;
    private Integer avgProcessedTime;

}