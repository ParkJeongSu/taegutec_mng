package kr.co.aim.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsCraneCreateRequestDto {

    @NotBlank(message = "크레인 명은 필수 입력 항목입니다.")
    private String craneName;

    @NotNull(message = "크레인 번호는 필수 입력 항목입니다.")
    private Integer craneNumber;

    @NotBlank(message = "공장 구분은 필수 입력 항목입니다.")
    private String factoryName;

    @NotNull(message = "로컬 번호는 필수 입력 항목입니다.")
    private Integer localNo;

    @NotBlank(message = "스토커 명은 필수 입력 항목입니다.")
    private String stockerName;

    private String autoRunStatus;
    private String carrierExist;
    private String carrierName;
    private String columnPosition;
    private String currentCmdData;
    private String errorHappen;
    private String forkPreStatus;
    private String forkStatus;
    private String jobCompleteState;
    private String mode;
    private String positionType;
    private String preStatus;
    private String rowPosition;
    private String stagePosition;
    private String status;
    private Integer touchPanelNumber;
    private String zoneName;
    private String craneReadingEnableMode;
    private String craneRfidEnableMode;
    private String portNoPosition;
    private Integer permissionRetryCount;
    private Integer permissionRetryTime;
    private Boolean opportunisticEnabled;

    private String eventName;
    private String eventUser;
    private String eventComment;
}
