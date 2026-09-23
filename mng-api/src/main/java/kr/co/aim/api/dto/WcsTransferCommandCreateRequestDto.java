package kr.co.aim.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsTransferCommandCreateRequestDto {

    @NotBlank(message = "반송 명령 명은 필수 입력 항목입니다.")
    private String transferCommandName;

    private String carrierName;
    private String commandStatus;
    private LocalDateTime createTime;
    private String currentEquipmentName;
    private String hotLot;
    private LocalDateTime jobCompletedTime;
    private LocalDateTime jobReceiveTime;
    private LocalDateTime jobStartTime;
    private String lotName;
    private String owner;
    private Integer priority;
    private Integer productQuantity;
    private String source;
    private String target;
    private String targetEquipmentName;
    private String factoryName;
    private String currentSource;
    private String orderType;
    private String sourceEquipmentName;
    private String sourceTransferType;
    private String targetTransferType;
    private Integer subCommandJobNo;
    private String subCommandStatus;
    private Integer transferSpeed;
    private String processType;
    private Boolean startReportFlag;

    private String eventName;
    private String eventUser;
    private String eventComment;
}
