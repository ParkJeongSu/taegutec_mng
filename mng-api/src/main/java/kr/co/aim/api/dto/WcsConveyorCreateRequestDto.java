package kr.co.aim.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsConveyorCreateRequestDto {

    @NotBlank(message = "컨베이어 그룹은 필수 입력 항목입니다.")
    private String conveyorGroup;

    @NotBlank(message = "컨베이어 명은 필수 입력 항목입니다.")
    private String conveyorName;

    @NotNull(message = "컨베이어 번호는 필수 입력 항목입니다.")
    private Integer conveyorNumber;

    @NotBlank(message = "공장 구분은 필수 입력 항목입니다.")
    private String factoryName;

    @NotNull(message = "로컬 번호는 필수 입력 항목입니다.")
    private Integer localNo;

    private String autoRunStatus;
    private String carrierExist;
    private String carrierName;
    private Integer conveyorGroupNumber;
    private String conveyorType;
    private String currentCmdData;
    private String direction;
    private String dispatchingPriority;
    private String errorHappen;
    private String jobCompleteState;
    private String onlineControlStatus;
    private String operationMode;
    private String preStatus;
    private Integer rtvNumber;
    private String status;
    private Integer touchPanelNumber;
    private String serverName;
    private String mode;
    private Integer downConveyorCount;
    private Integer onCarrierCount;
    private Integer totalConveyorCount;
    private Integer runConveyorCount;
    private String machineTypeName;
    private String readingEnableMode;
    private String rfidEnableMode;
    private String eqRouteKey;
    private String conveyorConnectionStatus;
    private String areaName;

    private String eventName;
    private String eventUser;
    private String eventComment;
}
