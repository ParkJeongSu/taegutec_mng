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
@Schema(description = "존(ZONE) 수정 요청 DTO")
public class ZoneUpdateRequestDto {

    @Schema(description = "고유 ID (TSID)", example = "877810665130787535")
    private Long id;

    @Schema(description = "존 이름", example = "ZONE-A")
    private String zoneName;

    @Schema(description = "존 타입", example = "STORAGE")
    private String zoneType;

    @Schema(description = "설명", example = "Storage zone A")
    private String description;

    @Schema(description = "존 색상", example = "#FFFFFF")
    private String zoneColor;

    @Schema(description = "이벤트 이름", example = "ZoneModified")
    private String eventName;

    @Schema(description = "이벤트 작업자", example = "SYSTEM")
    private String eventUser;

    @Schema(description = "이벤트 코멘트", example = "Zone modified")
    private String eventComment;
}
