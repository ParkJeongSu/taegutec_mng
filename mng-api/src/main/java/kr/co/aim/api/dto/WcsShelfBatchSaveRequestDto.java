package kr.co.aim.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "사용자 그룹별 메뉴 권한 일괄 저장 요청 DTO")
public class WcsShelfBatchSaveRequestDto {

    @Schema(description = "공장 구분", example = "INSERT", requiredMode = Schema.RequiredMode.REQUIRED)
    private String factoryName;

    @Schema(description = "ZONE NAME (ZONE.zoneName)", example = "C1B", requiredMode = Schema.RequiredMode.REQUIRED)
    private String zoneName;

    @Schema(description = "이벤트 이름", example = "UserGroupMenuAuthBatchSaved")
    private String eventName;

    @Schema(description = "이벤트 작업자", example = "SYSTEM")
    private String eventUser;

    @Schema(description = "이벤트 코멘트", example = "Batch menu auth saved")
    private String eventComment;

    @Schema(description = "Shelf 목록")
    private List<Shelf> shelfList;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "단일 메뉴 권한 설정 항목")
    public static class Shelf {

        @Schema(description = "공장 구분", example = "INSERT", requiredMode = Schema.RequiredMode.REQUIRED)
        private String factoryName;

        @Schema(description = "공장 구분", example = "01001101", requiredMode = Schema.RequiredMode.REQUIRED)
        private String shelfName;

        @Schema(description = "공장 구분", example = "WH1", requiredMode = Schema.RequiredMode.REQUIRED)
        private String stockerName;
    }
}
