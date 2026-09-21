package kr.co.aim.api.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.aim.domain.model.SysUser;
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
@Schema(description = "사용자 조회 응답 DTO")
public class SysUserResponseDto {

    @Schema(description = "고유 ID", example = "877810665130787535")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Schema(description = "공장 구분", example = "INSERT")
    private String factoryName;

    @Schema(description = "사용자 ID", example = "admin")
    private String userId;

    @Schema(description = "사용자 이름", example = "관리자")
    private String userName;

    @Schema(description = "부서 ID", example = "1")
    private Long departmentId;

    @Schema(description = "이메일", example = "admin@taegutec.co.kr")
    private String email;

    @Schema(description = "전화번호", example = "010-1234-5678")
    private String phoneNumber;

    @Schema(description = "사용자 상태", example = "ACTIVE")
    private String userState;

    @Schema(description = "로그인 실패 횟수", example = "0")
    private Integer failedLoginCount;

    @Schema(description = "마지막 로그인 시간", example = "2026-09-21T13:00:00")
    private LocalDateTime lastLoginTime;

    @Schema(description = "비밀번호 변경 시간", example = "2026-09-21T13:00:00")
    private LocalDateTime passwordChangeTime;

    @Schema(description = "이벤트 이름", example = "UserCreated")
    private String eventName;

    @Schema(description = "이벤트 시간", example = "2026-09-21T13:00:00")
    private LocalDateTime eventTime;

    @Schema(description = "이벤트 작업자", example = "SYSTEM")
    private String eventUser;

    @Schema(description = "이벤트 코멘트", example = "Initial Admin User")
    private String eventComment;

    public static SysUserResponseDto fromDomain(SysUser user) {
        if (user == null) {
            return null;
        }
        return SysUserResponseDto.builder()
                .id(user.getId())
                .factoryName(user.getFactoryName())
                .userId(user.getUserId())
                .userName(user.getUserName())
                .departmentId(user.getDepartmentId())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .userState(user.getUserState())
                .failedLoginCount(user.getFailedLoginCount())
                .lastLoginTime(user.getLastLoginTime())
                .passwordChangeTime(user.getPasswordChangeTime())
                .eventName(user.getEventName())
                .eventTime(user.getEventTime())
                .eventUser(user.getEventUser())
                .eventComment(user.getEventComment())
                .build();
    }
}
