package kr.co.aim.domain.model;

import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class WorkOrderProcessedDaily {

    private Long id;
    private String statDate;
    private Integer totalProcessedCount;
    private Integer avgProcessedTime;
    private BigDecimal totalQuantity;

}