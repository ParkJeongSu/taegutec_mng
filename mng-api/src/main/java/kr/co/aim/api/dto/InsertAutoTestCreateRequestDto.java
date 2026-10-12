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
@Schema(description = "INSERT 자동 테스트 생성 요청 DTO")
public class InsertAutoTestCreateRequestDto {

    @Schema(description = "창고 이름", example = "WH-01")
    private String wareHouseName;

    @Schema(description = "작업 스테이션 ID", example = "WS-01", requiredMode = Schema.RequiredMode.REQUIRED)
    private String workStationId;

    @Schema(description = "캐리어 이름", example = "CARRIER-001", requiredMode = Schema.RequiredMode.REQUIRED)
    private String carrierName;

    @Schema(description = "존 이름", example = "ZONE-A")
    private String zoneName;

    @Schema(description = "InboundPort Name", example = "314001")
    private String inboundPortName;

    @Schema(description = "주행 프로파일", example = "PROFILE_DEFAULT")
    private String travelProfile;

    @Schema(description = "이벤트 이름", example = "AutoTestCreated")
    private String eventName;

    @Schema(description = "이벤트 작업자", example = "SYSTEM")
    private String eventUser;

    @Schema(description = "이벤트 코멘트", example = "Auto test scenario created")
    private String eventComment;
}
