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
@Schema(description = "사이트(SITE) 생성 요청 DTO")
public class SiteCreateRequestDto {

    @Schema(description = "사이트명", example = "SITE-01", requiredMode = Schema.RequiredMode.REQUIRED)
    private String siteName;

    @Schema(description = "설명", example = "Main site")
    private String description;

    @Schema(description = "이벤트 이름", example = "SiteCreated")
    private String eventName;

    @Schema(description = "이벤트 작업자", example = "SYSTEM")
    private String eventUser;

    @Schema(description = "이벤트 코멘트", example = "Site created")
    private String eventComment;
}
