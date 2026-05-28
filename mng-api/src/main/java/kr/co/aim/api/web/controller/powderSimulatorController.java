package kr.co.aim.api.web.controller;

import kr.co.aim.api.dto.ProductionOrderSimulatorRequestDto;
import kr.co.aim.api.service.*;
import kr.co.aim.common.dto.powder.IdocH2TransResponseDto;
import kr.co.aim.common.dto.powder.IdocOrderMasterResponseDto;
import kr.co.aim.domain.model.ProductionOrder;
import kr.co.aim.infra.persistence.db2entity.powder.H2OrderDPEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/simulator")
@RequiredArgsConstructor
@Slf4j
@Profile({"simulator"})
@ConditionalOnProperty(name = "factory.type", havingValue = "powder")
public class powderSimulatorController {
    private final PowderSimulatorFacade powderSimulatorFacade;
    private final ProductionOrderService productionOrderService;
    private final PowderExternalInterfaceService powderExternalInterfaceService;


    @GetMapping("/inbound/idocs/{idoc-typ-id}")
    public ResponseEntity<Page<IdocOrderMasterResponseDto>> getInboundIdocList(
            @PathVariable("idoc-typ-id") Long idocTypId,
            @PageableDefault(page = 0, size = 10, sort = "lineId", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<IdocOrderMasterResponseDto> result = powderExternalInterfaceService.findIdocWithOrderMasterByIdocTypId(idocTypId,pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/h2trans/{gal-key}")
    public ResponseEntity<Page<IdocH2TransResponseDto>> getInboundH2TransList(
            @PathVariable("gal-key") String galKey,
            @PageableDefault(page = 0, size = 10, sort = "lineId", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<IdocH2TransResponseDto> result = powderExternalInterfaceService.findIdocWithH2TransByGalKey(galKey,pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/order-detail/{idocId}")
    public ResponseEntity<Page<H2OrderDPEntity>> getH2OrderDetailList(
            @PathVariable("idocId") Long idocId,
            @PageableDefault(page = 0, size = 10, sort = "lineId", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<H2OrderDPEntity> result = powderExternalInterfaceService.findByIdocId(idocId,pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/production-order/{h2-order-dp-line-id}")
    public ResponseEntity<Page<ProductionOrder>> getProductionOrder(
            @PathVariable("h2-order-dp-line-id") Long h2OrderDpLineId,
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Optional<ProductionOrder> optionalProductionOrder = productionOrderService.findByH2OrderDpLineId(h2OrderDpLineId);
        if(optionalProductionOrder.isPresent()){
            ProductionOrder productionOrder  = optionalProductionOrder.get();
            List<ProductionOrder> productionOrderList = Collections.singletonList(productionOrder);
            return ResponseEntity.ok(new PageImpl<>(productionOrderList,pageable,productionOrderList.size()));
        }else{
            throw new RuntimeException("production order not found");
        }
    }

    @PostMapping("/line-no-completed/{production-order-id}")
    public ResponseEntity<Page<ProductionOrder>> inboundLineNoCompleted(
            @PathVariable("production-order-id") Long productionOrderId
    ){
        ProductionOrder productionOrder = powderSimulatorFacade.orderLineNoCompletedInbound(productionOrderId);
        List<ProductionOrder> productionOrderList = Collections.singletonList(productionOrder);
        return ResponseEntity.ok(new PageImpl<>(productionOrderList, org.springframework.data.domain.PageRequest.of(0, 1), 1));
    }

    @PostMapping("/order-completed/{production-order-id}")
    public ResponseEntity<Page<ProductionOrder>> orderCompleted(
            @PathVariable("production-order-id") Long productionOrderId
    ){
        ProductionOrder productionOrder = powderSimulatorFacade.orderCompleted(productionOrderId);
        List<ProductionOrder> productionOrderList = Collections.singletonList(productionOrder);
        return ResponseEntity.ok(new PageImpl<>(productionOrderList, org.springframework.data.domain.PageRequest.of(0, 1), 1));
    }

    // inbound : 입고

    @PostMapping("/inbound/transfer/{h2order-dp-line-id}")
    public ResponseEntity<Page<ProductionOrder>> inboundTransfer(
            @PathVariable("h2order-dp-line-id") Long h2orderDpLineId
    ){
        ProductionOrder productionOrder = powderSimulatorFacade.transferInbound(h2orderDpLineId);
        List<ProductionOrder> productionOrderList = Collections.singletonList(productionOrder);
        return ResponseEntity.ok(new PageImpl<>(productionOrderList, org.springframework.data.domain.PageRequest.of(0, 1), 1));
    }

    @PostMapping("/inbound/accept/{production-order-id}")
    public ResponseEntity<Page<ProductionOrder>> inboundAccept(
            @PathVariable("production-order-id") Long productionOrderId
    ){
        ProductionOrder productionOrder = powderSimulatorFacade.acceptInbound(productionOrderId);
        List<ProductionOrder> productionOrderList = Collections.singletonList(productionOrder);
        return ResponseEntity.ok(new PageImpl<>(productionOrderList, org.springframework.data.domain.PageRequest.of(0, 1), 1));
    }

    @PostMapping("/inbound/release/{production-order-id}")
    public ResponseEntity<Page<ProductionOrder>> inboundRelease(
            @PathVariable("production-order-id") Long productionOrderId
    ){
        ProductionOrder productionOrder = powderSimulatorFacade.releaseInbound(productionOrderId);
        List<ProductionOrder> productionOrderList = Collections.singletonList(productionOrder);
        return ResponseEntity.ok(new PageImpl<>(productionOrderList, org.springframework.data.domain.PageRequest.of(0, 1), 1));
    }

    @PostMapping("/inbound/fibc-on-pallet/{production-order-id}")
    public ResponseEntity<Page<ProductionOrder>> inboundFibcOnPallet(
            @PathVariable("production-order-id") Long productionOrderId,
            @RequestBody ProductionOrderSimulatorRequestDto dto
    ){
        ProductionOrder productionOrder = powderSimulatorFacade.fibcOnPalletInbound(productionOrderId,dto);
        List<ProductionOrder> productionOrderList = Collections.singletonList(productionOrder);
        return ResponseEntity.ok(new PageImpl<>(productionOrderList, org.springframework.data.domain.PageRequest.of(0, 1), 1));
    }

    @PostMapping("/inbound/pallet-loadcompleted-warehouse/{production-order-id}")
    public ResponseEntity<Page<ProductionOrder>> inboundPalletLoadCompletedWarehouse(
            @PathVariable("production-order-id") Long productionOrderId,
            @RequestBody ProductionOrderSimulatorRequestDto dto
    ){
        ProductionOrder productionOrder = powderSimulatorFacade.palletLoadCompletedToWarehouseInbound(productionOrderId,dto);
        List<ProductionOrder> productionOrderList = Collections.singletonList(productionOrder);
        return ResponseEntity.ok(new PageImpl<>(productionOrderList, org.springframework.data.domain.PageRequest.of(0, 1), 1));
    }

    // unpacker : 해포

    @PostMapping("/unpacker/transfer/{h2order-dp-line-id}")
    public ResponseEntity<Page<ProductionOrder>> unpackerTransfer(
            @PathVariable("h2order-dp-line-id") Long h2orderDpLineId
    ){
        ProductionOrder productionOrder = powderSimulatorFacade.transferUnpacker(h2orderDpLineId);
        List<ProductionOrder> productionOrderList = Collections.singletonList(productionOrder);
        return ResponseEntity.ok(new PageImpl<>(productionOrderList, org.springframework.data.domain.PageRequest.of(0, 1), 1));
    }

    @PostMapping("/unpacker/accept/{production-order-id}")
    public ResponseEntity<Page<ProductionOrder>> unpackerAccept(
            @PathVariable("production-order-id") Long productionOrderId
    ){
        ProductionOrder productionOrder = powderSimulatorFacade.acceptUnpacker(productionOrderId);
        List<ProductionOrder> productionOrderList = Collections.singletonList(productionOrder);
        return ResponseEntity.ok(new PageImpl<>(productionOrderList, org.springframework.data.domain.PageRequest.of(0, 1), 1));
    }

    @PostMapping("/unpacker/release/{production-order-id}")
    public ResponseEntity<Page<ProductionOrder>> unpackerRelease(
            @PathVariable("production-order-id") Long productionOrderId
    ){
        ProductionOrder productionOrder = powderSimulatorFacade.releaseUnpacker(productionOrderId);
        List<ProductionOrder> productionOrderList = Collections.singletonList(productionOrder);
        return ResponseEntity.ok(new PageImpl<>(productionOrderList, org.springframework.data.domain.PageRequest.of(0, 1), 1));
    }

    @PostMapping("/unpacker/start/{production-order-id}")
    public ResponseEntity<Page<ProductionOrder>> unpackerStarted(
            @PathVariable("production-order-id") Long productionOrderId,
            @RequestBody ProductionOrderSimulatorRequestDto dto
    ){
        ProductionOrder productionOrder = powderSimulatorFacade.startUnpacker(productionOrderId,dto);
        List<ProductionOrder> productionOrderList = Collections.singletonList(productionOrder);
        return ResponseEntity.ok(new PageImpl<>(productionOrderList, org.springframework.data.domain.PageRequest.of(0, 1), 1));
    }

    @PostMapping("/unpacker/end/{production-order-id}")
    public ResponseEntity<Page<ProductionOrder>> unpackerEnded(
            @PathVariable("production-order-id") Long productionOrderId,
            @RequestBody ProductionOrderSimulatorRequestDto dto
    ){
        ProductionOrder productionOrder = powderSimulatorFacade.endUnpacker(productionOrderId,dto);
        List<ProductionOrder> productionOrderList = Collections.singletonList(productionOrder);
        return ResponseEntity.ok(new PageImpl<>(productionOrderList, org.springframework.data.domain.PageRequest.of(0, 1), 1));
    }



}
