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
@Schema(description = "사용자 수정 요청 DTO")
public class SysUserUpdateRequestDto {

    @Schema(description = "사용자 고유 ID", example = "877810665130787535")
    private Long id;

    @Schema(description = "공장 구분", example = "INSERT")
    private String factoryName;

    @Schema(description = "비밀번호 (변경 시에만 입력)", example = "newPassword123!")
    private String password;

    @Schema(description = "사용자 이름", example = "홍길동")
    private String userName;

    @Schema(description = "부서 ID", example = "1")
    private Long departmentId;

    @Schema(description = "이메일", example = "hong@taegutec.co.kr")
    private String email;

    @Schema(description = "전화번호", example = "010-9876-5432")
    private String phoneNumber;

    @Schema(description = "사용자 상태", example = "ACTIVE")
    private String userState;

    @Schema(description = "로그인 실패 횟수", example = "0")
    private Integer failedLoginCount;

    @Schema(description = "이벤트 이름", example = "UserModified")
    private String eventName;

    @Schema(description = "이벤트 작업자", example = "admin")
    private String eventUser;

    @Schema(description = "이벤트 코멘트", example = "사용자 정보 수정")
    private String eventComment;
}
