package kr.co.aim.api.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.aim.domain.model.InsertAutoTestResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "INSERT 자동 테스트 결과 응답 DTO")
public class InsertAutoTestResultResponse {

    @Schema(description = "고유 ID (TSID)", example = "877810665130787535")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Schema(description = "자동 테스트 마스터 ID (TSID)", example = "877810665130787535")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long autoTestId;

    @Schema(description = "테스트 진행 상태", example = "COMPLETED")
    private String state;

    @Schema(description = "테스트 수행 결과", example = "SUCCESS")
    private String result;

    @Schema(description = "이벤트 이름", example = "TestResultRecorded")
    private String eventName;

    @Schema(description = "이벤트 시간", example = "2026-10-08T09:00:00")
    private LocalDateTime eventTime;

    @Schema(description = "이벤트 작업자", example = "SYSTEM")
    private String eventUser;

    @Schema(description = "이벤트 코멘트", example = "Round-trip completed successfully")
    private String eventComment;

    public static InsertAutoTestResultResponse fromDomain(InsertAutoTestResult testResult) {
        if (testResult == null) {
            return null;
        }
        return InsertAutoTestResultResponse.builder()
                .id(testResult.getId())
                .autoTestId(testResult.getAutoTestId())
                .state(testResult.getState())
                .result(testResult.getResult())
                .eventName(testResult.getEventName())
                .eventTime(testResult.getEventTime())
                .eventUser(testResult.getEventUser())
                .eventComment(testResult.getEventComment())
                .build();
    }
}
