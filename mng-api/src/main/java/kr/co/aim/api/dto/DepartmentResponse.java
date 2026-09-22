package kr.co.aim.api.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.aim.domain.model.Department;
import kr.co.aim.infra.persistence.entity.DepartmentEntity;
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
@Schema(description = "부서 조회 응답 DTO")
public class DepartmentResponse {

    @Schema(description = "고유 ID (TSID)", example = "877810665130787535")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Schema(description = "공장 구분", example = "INSERT")
    private String factoryName;

    @Schema(description = "부서명", example = "생산관리팀")
    private String departmentName;

    @Schema(description = "사용 여부 (상태)", example = "ACTIVE")
    private String useState;

    @Schema(description = "이벤트 이름", example = "DepartmentCreated")
    private String eventName;

    @Schema(description = "이벤트 시간", example = "2026-09-22T09:00:00")
    private LocalDateTime eventTime;

    @Schema(description = "이벤트 작업자", example = "SYSTEM")
    private String eventUser;

    @Schema(description = "이벤트 코멘트", example = "Initial Department")
    private String eventComment;

    public static DepartmentResponse fromEntity(DepartmentEntity entity) {
        if (entity == null) {
            return null;
        }
        return DepartmentResponse.builder()
                .id(entity.getId())
                .factoryName(entity.getFactoryName())
                .departmentName(entity.getDepartmentName())
                .useState(entity.getUseState())
                .eventName(entity.getEventName())
                .eventTime(entity.getEventTime())
                .eventUser(entity.getEventUser())
                .eventComment(entity.getEventComment())
                .build();
    }

    public static DepartmentResponse fromDomain(Department domain) {
        if (domain == null) {
            return null;
        }
        return DepartmentResponse.builder()
                .id(domain.getId())
                .factoryName(domain.getFactoryName())
                .departmentName(domain.getDepartmentName())
                .useState(domain.getUseState())
                .eventName(domain.getEventName())
                .eventTime(domain.getEventTime())
                .eventUser(domain.getEventUser())
                .eventComment(domain.getEventComment())
                .build();
    }
}
