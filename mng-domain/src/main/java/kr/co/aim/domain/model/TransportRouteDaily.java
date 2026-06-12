package kr.co.aim.domain.model;

import lombok.*;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class TransportRouteDaily {


    private IdTransportRouteDaily id;
    private Integer totalCount;
    private Integer errorCount;
    private Integer avgAcquireTimeSec;
    private Integer maxAcquireTimeSec;
    private Integer avgTransferTimeSec;
    private Integer maxTransferTimeSec;
    private Integer avgCycleTimeSec;
    private Integer maxCycleTimeSec;

}