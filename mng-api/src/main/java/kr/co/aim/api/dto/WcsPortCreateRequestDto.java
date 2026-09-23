package kr.co.aim.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsPortCreateRequestDto {

    @NotBlank(message = "설비 명은 필수 입력 항목입니다.")
    private String equipmentName;

    @NotBlank(message = "공장 구분은 필수 입력 항목입니다.")
    private String factoryName;

    @NotNull(message = "로컬 번호는 필수 입력 항목입니다.")
    private Integer localNo;

    @NotNull(message = "포트 번호는 필수 입력 항목입니다.")
    private Integer portNumber;

    private String carrierName;
    private String errorHappen;
    private String portContainStatus;
    private String portEnableMode;
    private String portName;
    private String portStatus;
    private String portTransferStatus;
    private String portType;
    private Integer touchPanelNumber;
    private String zoneName;
    private Integer bin;
    private Integer row;
    private Integer stage;
    private Integer col;
    private String linkEquipmentName;
    private String linkPortName;
    private String linkPortType;
    private String portTransferMode;
    private String portReadingEnableMode;
    private String portDetailType;
    private String rejectEquipmentName;
    private String rejectPortName;
    private Boolean useWorkerFlag;
    private String portUseType;
    private String portMode;
    private String portOperationMode;
    private String portRfidEnableMode;

    private String eventName;
    private String eventUser;
    private String eventComment;
}
