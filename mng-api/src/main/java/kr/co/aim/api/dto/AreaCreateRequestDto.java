package kr.co.aim.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "구역(AREA) 생성 요청 DTO")
public class AreaCreateRequestDto {

    @Schema(description = "구역명", example = "AREA-01", requiredMode = Schema.RequiredMode.REQUIRED)
    private String areaName;

    @Schema(description = "설명", example = "Main area")
    private String description;

    @Schema(description = "이벤트 이름", example = "AreaCreated")
    private String eventName;

    @Schema(description = "이벤트 작업자", example = "SYSTEM")
    private String eventUser;

    @Schema(description = "이벤트 코멘트", example = "Area created")
    private String eventComment;
}
