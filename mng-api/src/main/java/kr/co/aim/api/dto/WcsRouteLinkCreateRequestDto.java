package kr.co.aim.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsRouteLinkCreateRequestDto {

    @NotBlank(message = "공장 구분은 필수 입력 항목입니다.")
    private String factoryName;

    @NotNull(message = "경로 링크 ID는 필수 입력 항목입니다.")
    private Long routeLinkId;

    private String description;
    private Long fromNodeId;
    private Integer length;
    private String passYn;
    private Integer priority;
    private String processType;
    private String routeLinkType;
    private Long toNodeId;
    private String usableYn;
    private String useYn;

    private String eventName;
    private String eventUser;
    private String eventComment;
}
