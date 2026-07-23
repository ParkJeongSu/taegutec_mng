package kr.co.aim.api.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.aim.api.service.CarrierService;
import kr.co.aim.common.annotation.ResponseAnnotation;
import kr.co.aim.common.condition.*;
import kr.co.aim.common.dto.CarrierLotSearchResultDto;
import kr.co.aim.domain.model.Carrier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "MNG Carrier", description = "MNG Carrier 관련 API")
@RestController
@RequestMapping("/api/v1/mng/carrier")
@RequiredArgsConstructor
@Slf4j
@Profile("web")
@ResponseAnnotation
@ConditionalOnProperty(name = "factory.type", havingValue = "powder")
public class CarrierController {
    private final CarrierService carrierService;

    @Operation(summary = "Carrier", description = "Carrier 조회")
    @GetMapping("")
    public ResponseEntity<Page<Carrier>> getProductionOrder(
            CarrierSearchCondition condition,
            @org.springdoc.core.annotations.ParameterObject
            Pageable pageable
    ) {
        Page<Carrier> reuslt = carrierService.findCarrierByCondition(condition,pageable);
        return ResponseEntity.ok(reuslt);
    }

    @Operation(summary = "Carrier-Lot", description = "Carrier Lot 종합 조회")
    @GetMapping("/with-lot")
    public ResponseEntity<Page<CarrierLotSearchResultDto>> getProductionOrder(
            CarrierLotSearchCondition condition,
            @org.springdoc.core.annotations.ParameterObject
            Pageable pageable
    ) {
        Page<CarrierLotSearchResultDto> reuslt = carrierService.findCarrierLotByCondition(condition,pageable);
        return ResponseEntity.ok(reuslt);
    }


}
