package kr.co.aim.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.*;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class TransportRouteDaily {
    private Long id;
    private String statDate;
    private String sourceEquipmentName;
    private String destinationEquipmentName;
    private Integer totalCount;
    private Integer errorCount;
    private Integer avgAcquireTimeSec;
    private Integer maxAcquireTimeSec;
    private Integer avgTransferTimeSec;
    private Integer maxTransferTimeSec;
    private Integer avgCycleTimeSec;
    private Integer maxCycleTimeSec;

}