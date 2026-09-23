package kr.co.aim.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsAlternativeStorageZoneCreateRequestDto {

    @NotBlank(message = "공장 구분은 필수 입력 항목입니다.")
    private String factoryName;

    @NotBlank(message = "원본 보관 존 명은 필수 입력 항목입니다.")
    private String sourceZoneName;

    @NotBlank(message = "대체 보관 존 명은 필수 입력 항목입니다.")
    private String alternativeZoneName;

    @NotNull(message = "우선순위는 필수 입력 항목입니다.")
    private Integer priority;

    private String description;
    private String useYn;

    private String eventName;
    private String eventUser;
    private String eventComment;
}
