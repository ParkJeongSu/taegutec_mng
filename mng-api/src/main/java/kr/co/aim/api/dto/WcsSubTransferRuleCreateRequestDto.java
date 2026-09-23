package kr.co.aim.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsSubTransferRuleCreateRequestDto {

    @NotBlank(message = "설비 명은 필수 입력 항목입니다.")
    private String equipmentName;

    @NotBlank(message = "공장 구분은 필수 입력 항목입니다.")
    private String factoryName;

    @NotBlank(message = "모듈 명은 필수 입력 항목입니다.")
    private String moduleName;

    @NotNull(message = "경로 링크 ID는 필수 입력 항목입니다.")
    private Long routeLinkId;

    private Integer carrierCount;
    private String description;
    private String moduleType;
    private String ngStatus;

    private String eventName;
    private String eventUser;
    private String eventComment;
}
