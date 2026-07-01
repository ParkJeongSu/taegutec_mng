package kr.co.aim.domain.model;

import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class EquipmentProductivityDaily {
    private Long id;
    private String statDate;
    private String equipmentName;
    private Integer totalProcessedCount;
    private BigDecimal totalProcessedQuantity;
    private BigDecimal okProcessed;
    private BigDecimal ngProcessed;
    private Integer avgProcessedTime;

}