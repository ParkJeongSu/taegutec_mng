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
@Schema(description = "존 폴백(ZONE_FALLBACK) 수정 요청 DTO")
public class ZoneFallbackUpdateRequestDto {

    @Schema(description = "고유 ID (TSID)", example = "877810665130787535")
    private Long id;

    @Schema(description = "원본 존 ID", example = "877810665130787535")
    private Long sourceZoneId;

    @Schema(description = "대상 존 ID", example = "877810665130787536")
    private Long targetZoneId;

    @Schema(description = "우선순위", example = "1")
    private Integer priority;

    @Schema(description = "이벤트 이름", example = "ZoneFallbackModified")
    private String eventName;

    @Schema(description = "이벤트 작업자", example = "SYSTEM")
    private String eventUser;

    @Schema(description = "이벤트 코멘트", example = "ZoneFallback modified")
    private String eventComment;
}
