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
@Schema(description = "선반(SHELF) 생성 요청 DTO")
public class ShelfCreateRequestDto {

    @Schema(description = "위치 ID", example = "LOC-001", requiredMode = Schema.RequiredMode.REQUIRED)
    private String locationId;

    @Schema(description = "설비명", example = "STK-01")
    private String equipmentName;

    @Schema(description = "존 ID", example = "877810665130787535")
    private Long zoneId;

    @Schema(description = "구역 ID", example = "877810665130787536")
    private Long areaId;

    @Schema(description = "적재 용량", example = "1")
    private Integer capacity;

    @Schema(description = "선반 상태", example = "EMPTY")
    private String shelfState;

    @Schema(description = "통신 상태", example = "ONLINE")
    private String communicationState;

    @Schema(description = "홀드 상태", example = "NONE")
    private String holdState;

    @Schema(description = "길이", example = "100")
    private Integer length;

    @Schema(description = "너비", example = "100")
    private Integer width;

    @Schema(description = "높이", example = "100")
    private Integer height;

    @Schema(description = "이벤트 이름", example = "ShelfCreated")
    private String eventName;

    @Schema(description = "이벤트 작업자", example = "SYSTEM")
    private String eventUser;

    @Schema(description = "이벤트 코멘트", example = "Shelf created")
    private String eventComment;
}
