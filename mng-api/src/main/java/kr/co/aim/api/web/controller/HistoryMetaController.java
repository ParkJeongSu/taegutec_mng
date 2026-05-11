package kr.co.aim.api.web.controller;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.aim.api.service.HistoryMetaService;
import kr.co.aim.common.format.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Hidden
@Tag(name = "History-meta", description = "History-meta 관련 API")
//@RestController
//@RequestMapping("/api/history-meta")
@RequiredArgsConstructor
public class HistoryMetaController {

    private final HistoryMetaService historyMetaService;

    @Operation(summary = "history 메타 정보 조회", description = "history 메타 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "history 정보를 찾을 수 없음")
    })
    // 1. 요청 접수: GET /api/history-meta/resources
    @GetMapping("/resources")
    public ResponseEntity<List<String>> getHistoryResources(@RequestParam String system) {
        // 3. 서비스 계층에 작업 위임
        List<String> resourceNames = historyMetaService.getResourcesBySystem(system);

        // 4. 결과 변환 및 HTTP 응답
        return ResponseEntity.ok(resourceNames);
    }


}
