package kr.co.aim.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsAlarmCreateRequestDto {

    @NotBlank(message = "알람 ID는 필수 입력 항목입니다.")
    private String alarmId;

    @NotBlank(message = "설비 명은 필수 입력 항목입니다.")
    private String equipmentName;

    @NotBlank(message = "공장 구분은 필수 입력 항목입니다.")
    private String factoryName;

    @NotNull(message = "레이어 번호는 필수 입력 항목입니다.")
    private Integer layerNumber;

    @NotBlank(message = "레이어 구분은 필수 입력 항목입니다.")
    private String layerType;

    private String alarmLevel;
    private String alarmRecoveryOptions;
    private String alarmText;
    private String layerName;
    private Boolean systemAlarmFlag;

    private String eventName;
    private String eventUser;
    private String eventComment;
}
