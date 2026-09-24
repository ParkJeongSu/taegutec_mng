package kr.co.aim.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsCarrierCreateRequestDto {

    @NotBlank(message = "캐리어 명은 필수 입력 항목입니다.")
    private String carrierName;

    @NotBlank(message = "공장 구분은 필수 입력 항목입니다.")
    private String factoryName;

    private String afterProcess;
    private String beforeProcess;
    private String carrierGroup;
    private String carrierStatus;
    private LocalDateTime createTime;
    private String currentPositionName;
    private String hotLot;
    private String lotName;
    private String owner;
    private String previousCarrierStatus;
    private String productQuantity;
    private String zoneName;
    private String currentEquipmentName;
    private String carrierDetailType;
    private String carrierType;
    private String transferCommandName;
    private String travelProfile;
    private String itemName;
    private String orderId;
    private Integer orderLineNumber;
    private String productionType;
    private LocalDateTime inboundTime;
    private LocalDateTime outboundTime;
    private String weight;
    private Integer carrierUseCount;

    private String eventName;
    private String eventUser;
    private String eventComment;
}
