package kr.co.aim.api.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.aim.api.service.TransportJobService;
import kr.co.aim.common.condition.TransportJobHistorySearchCondition;
import kr.co.aim.domain.model.TransportJobHistory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "MNG 반송잡 관리", description = "MNG 반송관련 API")
@RestController
@RequestMapping("/api/transport-job")
@RequiredArgsConstructor
@Slf4j
@Profile("web")
public class TransportJobController {
    private final TransportJobService transportJobService;

    @Operation(summary = "반송 history", description = "반송잡 history 조회")
    @GetMapping("/history")
    public ResponseEntity<Page<TransportJobHistory>> getHistory(
            TransportJobHistorySearchCondition condition,
            @org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        Page<TransportJobHistory> reuslt = transportJobService.findTransportJobHistoryByCondition(condition,pageable);
        return ResponseEntity.ok(reuslt);
    }
}
