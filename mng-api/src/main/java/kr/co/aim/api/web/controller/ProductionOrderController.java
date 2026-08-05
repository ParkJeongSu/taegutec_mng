package kr.co.aim.api.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.aim.api.service.ProductionOrderService;
import kr.co.aim.common.annotation.ResponseAnnotation;
import kr.co.aim.common.condition.*;
import kr.co.aim.domain.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "MNG ProductionOrder 관리", description = "MNG order관련 API")
@RestController
@RequestMapping("/api/v1/mng/production-order")
@RequiredArgsConstructor
@Slf4j
@ResponseAnnotation
@Profile("web")
@ConditionalOnProperty(name = "factory.type", havingValue = "powder")
public class ProductionOrderController {
    private final ProductionOrderService productionOrderService;

    @Operation(summary = "Production Order", description = "order Info 조회")
    @GetMapping("/summaries")
    public ResponseEntity<Page<ProductionOrderSummary>> getSummaries(
            ProductionOrderSummarySearchCondition condition,
            @PageableDefault(size = 100, sort = "createTime", direction = Sort.Direction.DESC)
            @org.springdoc.core.annotations.ParameterObject
            Pageable pageable
    ) {
        Page<ProductionOrderSummary> reuslt = productionOrderService.findProductionOrderSummaryByCondition(condition,pageable);
        return ResponseEntity.ok(reuslt);
    }

    @Operation(summary = "Production Order", description = "order Info 조회")
    @GetMapping("")
    public ResponseEntity<Page<ProductionOrder>> getProductionOrder(
            ProductionOrderSearchCondition condition,
            @PageableDefault(size = 100, sort = "createTime", direction = Sort.Direction.DESC)
            @org.springdoc.core.annotations.ParameterObject
            Pageable pageable
    ) {
        Page<ProductionOrder> reuslt = productionOrderService.findProductionOrderByCondition(condition,pageable);
        return ResponseEntity.ok(reuslt);
    }

    @Operation(summary = "order history", description = "order history 조회")
    @GetMapping("/history")
    public ResponseEntity<Page<ProductionOrderHistory>> getHistory(
            ProductionOrderHistorySearchCondition condition,
            @org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        Page<ProductionOrderHistory> reuslt = productionOrderService.findProductionOrderHistoryByCondition(condition,pageable);
        return ResponseEntity.ok(reuslt);
    }


}
