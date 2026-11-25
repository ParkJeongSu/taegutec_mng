package kr.co.aim.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "자재 로트(MATERIAL_LOT) 수정 요청 DTO")
public class MaterialLotUpdateRequestDto {

    @Schema(description = "고유 ID (TSID)", example = "877810665130787535")
    private Long id;

    @Schema(description = "자재 로트 ID", example = "MLOT-001")
    private String materialLotId;

    @Schema(description = "자재 정의 ID", example = "877810665130787535")
    private Long materialDefId;

    @Schema(description = "상위 자재 로트 ID", example = "877810665130787536")
    private Long parentMaterialLotId;

    @Schema(description = "캐리어 이름", example = "CARRIER-001")
    private String carrierName;

    @Schema(description = "수량", example = "100.000")
    private BigDecimal quantity;

    @Schema(description = "상태", example = "CREATED")
    private String state;

    @Schema(description = "이벤트 이름", example = "MaterialLotModified")
    private String eventName;

    @Schema(description = "이벤트 작업자", example = "SYSTEM")
    private String eventUser;

    @Schema(description = "이벤트 코멘트", example = "MaterialLot modified")
    private String eventComment;
}
