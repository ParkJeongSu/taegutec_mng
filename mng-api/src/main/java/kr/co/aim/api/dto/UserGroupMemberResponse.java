package kr.co.aim.api.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.aim.domain.model.UserGroupMember;
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
@Schema(description = "사용자-그룹 매핑 조회 응답 DTO (사용자 및 그룹 정보 조인 포함)")
public class UserGroupMemberResponse {

    @Schema(description = "매핑 고유 ID (TSID)", example = "877810665130787535")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Schema(description = "공장 구분", example = "INSERT")
    private String factoryName;

    @Schema(description = "사용자 고유 ID (SYS_USER.ID 참조키)", example = "877810665130787500")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    @Schema(description = "사용자 사번 (SYS_USER.USER_ID)", example = "admin")
    private String employeeId;

    @Schema(description = "사용자 성명 (SYS_USER.USER_NAME)", example = "관리자")
    private String userName;

    @Schema(description = "사용자 그룹 ID (USER_GROUP.ID 참조키)", example = "10")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userGroupId;

    @Schema(description = "사용자 그룹명 (USER_GROUP.USER_GROUP_NAME)", example = "ADMIN")
    private String userGroupName;

    @Schema(description = "사용자 그룹 설명 (USER_GROUP.DESCRIPTION)", example = "시스템 관리자 그룹")
    private String groupDescription;

    @Schema(description = "이벤트 이름", example = "UserGroupMemberCreated")
    private String eventName;

    @Schema(description = "이벤트 시간", example = "2026-09-21T13:00:00")
    private LocalDateTime eventTime;

    @Schema(description = "이벤트 작업자", example = "SYSTEM")
    private String eventUser;

    @Schema(description = "이벤트 코멘트", example = "Initial Group Assignment")
    private String eventComment;

    public static UserGroupMemberResponse fromDomain(UserGroupMember domain) {
        if (domain == null) {
            return null;
        }
        return UserGroupMemberResponse.builder()
                .id(domain.getId())
                .factoryName(domain.getFactoryName())
                .userId(domain.getUserId())
                .employeeId(domain.getEmployeeId())
                .userName(domain.getUserName())
                .userGroupId(domain.getUserGroupId())
                .userGroupName(domain.getUserGroupName())
                .groupDescription(domain.getGroupDescription())
                .eventName(domain.getEventName())
                .eventTime(domain.getEventTime())
                .eventUser(domain.getEventUser())
                .eventComment(domain.getEventComment())
                .build();
    }
}
