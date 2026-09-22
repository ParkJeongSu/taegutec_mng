package kr.co.aim.api.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.aim.domain.model.UserGroup;
import kr.co.aim.infra.persistence.entity.UserGroupEntity;
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
@Schema(description = "사용자 그룹 조회 응답 DTO")
public class UserGroupResponse {

    @Schema(description = "고유 ID (TSID)", example = "877810665130787535")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Schema(description = "공장 구분", example = "INSERT")
    private String factoryName;

    @Schema(description = "사용자 그룹명", example = "ADMIN_GROUP")
    private String userGroupName;

    @Schema(description = "설명", example = "시스템 관리자 그룹")
    private String description;

    @Schema(description = "사용 여부 (상태)", example = "ACTIVE")
    private String useState;

    @Schema(description = "이벤트 이름", example = "UserGroupCreated")
    private String eventName;

    @Schema(description = "이벤트 시간", example = "2026-09-22T09:00:00")
    private LocalDateTime eventTime;

    @Schema(description = "이벤트 작업자", example = "SYSTEM")
    private String eventUser;

    @Schema(description = "이벤트 코멘트", example = "Initial User Group")
    private String eventComment;

    public static UserGroupResponse fromEntity(UserGroupEntity entity) {
        if (entity == null) {
            return null;
        }
        return UserGroupResponse.builder()
                .id(entity.getId())
                .factoryName(entity.getFactoryName())
                .userGroupName(entity.getUserGroupName())
                .description(entity.getDescription())
                .useState(entity.getUseState())
                .eventName(entity.getEventName())
                .eventTime(entity.getEventTime())
                .eventUser(entity.getEventUser())
                .eventComment(entity.getEventComment())
                .build();
    }

    public static UserGroupResponse fromDomain(UserGroup domain) {
        if (domain == null) {
            return null;
        }
        return UserGroupResponse.builder()
                .id(domain.getId())
                .factoryName(domain.getFactoryName())
                .userGroupName(domain.getUserGroupName())
                .description(domain.getDescription())
                .useState(domain.getUseState())
                .eventName(domain.getEventName())
                .eventTime(domain.getEventTime())
                .eventUser(domain.getEventUser())
                .eventComment(domain.getEventComment())
                .build();
    }
}
