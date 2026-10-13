package kr.co.aim.api.simulator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConveyorTrayMovementDto {
    private String warehouseId;      // 예: "1"
    private String workStationId;    // 예: "311"
    private String carrierName;      // 예: "TRAY-311-01"
    private String positionId;        // 예: "pos-31101"
    private String previousPosition;  // 이전 포지션
    private String action;            // "ENTER", "MOVE", "REMOVE"
    private String state;             // "MOVING", "STOPPED", "WAITING"
    private LocalDateTime eventTime;
}