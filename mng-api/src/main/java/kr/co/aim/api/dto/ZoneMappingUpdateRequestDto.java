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
@Schema(description = "존 매핑(ZONE_MAPPING) 수정 요청 DTO")
public class ZoneMappingUpdateRequestDto {

    @Schema(description = "고유 ID (TSID)", example = "877810665130787535")
    private Long id;

    @Schema(description = "존 ID", example = "877810665130787535")
    private Long zoneId;

    @Schema(description = "오더 라인 번호", example = "0010")
    private String orderLineNumber;

    @Schema(description = "이벤트 이름", example = "ZoneMappingModified")
    private String eventName;

    @Schema(description = "이벤트 작업자", example = "SYSTEM")
    private String eventUser;

    @Schema(description = "이벤트 코멘트", example = "ZoneMapping modified")
    private String eventComment;
}
