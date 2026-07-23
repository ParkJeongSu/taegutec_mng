package kr.co.aim.api.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.aim.api.service.TransportOrderService;
import kr.co.aim.common.annotation.ResponseAnnotation;
import kr.co.aim.common.condition.TransportOrderSearchCondition;
import kr.co.aim.domain.model.TransportOrder;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@Tag(name = "MNG Transport Order", description = "기준정보 Transport Order 관리 API")
@RestController
@RequestMapping("/api/v1/mng/transport-order")
@RequiredArgsConstructor
@Profile("web")
@ResponseAnnotation
public class TransportOrderController {

    private final TransportOrderService transportOrderService;

    @Operation(summary = "Transport Order 목록 조회")
    @GetMapping("")
    public ResponseEntity<Page<TransportOrder>> getTransportOrders(
            TransportOrderSearchCondition condition,
            @ParameterObject Pageable pageable
    ) {
        Page<TransportOrder> result = transportOrderService.findTransportOrderWithConditions(condition, pageable);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Transport Order 상세 조회")
    @GetMapping("/{id}")
    public ResponseEntity<Page<TransportOrder>> getTransportOrder(@PathVariable("id") Long id) {
        TransportOrder result = transportOrderService.findById(id);
        return ResponseEntity.ok(new PageImpl<>(Collections.singletonList(result), PageRequest.of(0, 1), 1));
    }
}