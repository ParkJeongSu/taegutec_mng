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
@Schema(description = "사용자 등록 요청 DTO")
public class SysUserCreateRequestDto {

    @Schema(description = "공장 구분", example = "INSERT", requiredMode = Schema.RequiredMode.REQUIRED)
    private String factoryName;

    @Schema(description = "사용자 ID", example = "admin", requiredMode = Schema.RequiredMode.REQUIRED)
    private String userId;

    @Schema(description = "비밀번호 (평문)", example = "1234", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @Schema(description = "사용자 이름", example = "관리자", requiredMode = Schema.RequiredMode.REQUIRED)
    private String userName;

    @Schema(description = "부서 ID", example = "1")
    private Long departmentId;

    @Schema(description = "이메일", example = "admin@taegutec.co.kr")
    private String email;

    @Schema(description = "전화번호", example = "010-1234-5678")
    private String phoneNumber;

    @Schema(description = "사용자 상태", example = "ACTIVE")
    private String userState;

    @Schema(description = "이벤트 이름", example = "UserCreated")
    private String eventName;

    @Schema(description = "이벤트 작업자", example = "SYSTEM")
    private String eventUser;

    @Schema(description = "이벤트 코멘트", example = "Initial Admin User")
    private String eventComment;
}
