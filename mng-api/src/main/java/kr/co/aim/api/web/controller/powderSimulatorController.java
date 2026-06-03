package kr.co.aim.api.web.controller;

import kr.co.aim.api.dto.ProductionOrderSimulatorRequestDto;
import kr.co.aim.api.service.*;
import kr.co.aim.common.condition.ProductDefSearchCondition;
import kr.co.aim.common.dto.powder.IdocH2TransResponseDto;
import kr.co.aim.common.dto.powder.IdocOrderMasterResponseDto;
import kr.co.aim.domain.model.ProductDef;
import kr.co.aim.domain.model.ProductionOrder;
import kr.co.aim.infra.persistence.db2entity.powder.H2OrderDPEntity;
import kr.co.aim.infra.persistence.db2entity.powder.H2PartMPEntity;
import kr.co.aim.infra.persistence.db2entity.powder.IdocPEntity;
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
    private final ProductDefService productDefService;


    @GetMapping("/idocs/{idoc-typ-id}")
    public ResponseEntity<Page<IdocOrderMasterResponseDto>> getIdocList(
            @PathVariable("idoc-typ-id") Long idocTypId,
            @PageableDefault(page = 0, size = 10, sort = "lineId", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<IdocOrderMasterResponseDto> result = powderExternalInterfaceService.findIdocWithOrderMasterByIdocTypId(idocTypId,pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/idocs/part/{idoc-typ-id}")
    public ResponseEntity<Page<IdocPEntity>> getIdocListForPart(
            @PathVariable("idoc-typ-id") Long idocTypId,
            @PageableDefault(page = 0, size = 10, sort = "lineId", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<IdocPEntity> result = powderExternalInterfaceService.findByIdocTypId(idocTypId,pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/h2-part/{idoc-id}")
    public ResponseEntity<Page<H2PartMPEntity>> getH2PartList(
            @PathVariable("idoc-id") Long idocId,
            @PageableDefault(page = 0, size = 10, sort = "lineId", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<H2PartMPEntity> result = powderExternalInterfaceService.getPartList(idocId,pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/part")
    public ResponseEntity<Page<ProductDef>> getPartList(
            @ModelAttribute ProductDefSearchCondition condition,
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<ProductDef> productDefPage = productDefService.findProductDefByCondition(condition,pageable);
        return ResponseEntity.ok(productDefPage);
    }

    @GetMapping("/h2trans/{gal-key}")
    public ResponseEntity<Page<IdocH2TransResponseDto>> getH2TransList(
            @PathVariable("gal-key") String galKey,
            @PageableDefault(page = 0, size = 10, sort = "lineId", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<IdocH2TransResponseDto> result = powderExternalInterfaceService.findIdocWithH2TransByGalKey(galKey,pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/h2trans/part")
    public ResponseEntity<Page<IdocH2TransResponseDto>> getH2TransListByPart(
            @PageableDefault(page = 0, size = 10, sort = "lineId", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<IdocH2TransResponseDto> result = powderExternalInterfaceService.findIdocWithH2TransByPartIsNotNull(pageable);
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

    // part : part

    @PostMapping("/part/transfer/{idoc-id}")
    public ResponseEntity<Page<ProductDef>> partTransfer(
            @PathVariable("idoc-id") Long idocId
    ){
        Pageable pageable = Pageable.unpaged();
        //Pageable pageable = PageRequest.of(0,10);

        List<ProductDef> productDefList = powderSimulatorFacade.transferH2PartM(idocId,pageable);
        return ResponseEntity.ok(new PageImpl<>(productDefList, pageable, productDefList.size()));
    }

    // production : production 조업

    @PostMapping("/production/transfer/{h2order-dp-line-id}")
    public ResponseEntity<Page<ProductionOrder>> productionTransfer(
            @PathVariable("h2order-dp-line-id") Long h2orderDpLineId
    ){
        ProductionOrder productionOrder = powderSimulatorFacade.transferProduction(h2orderDpLineId);
        List<ProductionOrder> productionOrderList = Collections.singletonList(productionOrder);
        return ResponseEntity.ok(new PageImpl<>(productionOrderList, org.springframework.data.domain.PageRequest.of(0, 1), 1));
    }

    @PostMapping("/production/accept/{production-order-id}")
    public ResponseEntity<Page<ProductionOrder>> productionAccept(
            @PathVariable("production-order-id") Long productionOrderId
    ){
        ProductionOrder productionOrder = powderSimulatorFacade.acceptProduction(productionOrderId);
        List<ProductionOrder> productionOrderList = Collections.singletonList(productionOrder);
        return ResponseEntity.ok(new PageImpl<>(productionOrderList, org.springframework.data.domain.PageRequest.of(0, 1), 1));
    }

    @PostMapping("/production/release/{production-order-id}")
    public ResponseEntity<Page<ProductionOrder>> productionRelease(
            @PathVariable("production-order-id") Long productionOrderId
    ){
        ProductionOrder productionOrder = powderSimulatorFacade.releaseProduction(productionOrderId);
        List<ProductionOrder> productionOrderList = Collections.singletonList(productionOrder);
        return ResponseEntity.ok(new PageImpl<>(productionOrderList, org.springframework.data.domain.PageRequest.of(0, 1), 1));
    }

    @PostMapping("/production/start/{production-order-id}")
    public ResponseEntity<Page<ProductionOrder>> productionStart(
            @PathVariable("production-order-id") Long productionOrderId,
            @RequestBody ProductionOrderSimulatorRequestDto dto
    ){
        ProductionOrder productionOrder = powderSimulatorFacade.startProduction(productionOrderId,dto);
        List<ProductionOrder> productionOrderList = Collections.singletonList(productionOrder);
        return ResponseEntity.ok(new PageImpl<>(productionOrderList, org.springframework.data.domain.PageRequest.of(0, 1), 1));
    }

    @PostMapping("/production/end/{production-order-id}")
    public ResponseEntity<Page<ProductionOrder>> productionEnd(
            @PathVariable("production-order-id") Long productionOrderId,
            @RequestBody ProductionOrderSimulatorRequestDto dto
    ){
        ProductionOrder productionOrder = powderSimulatorFacade.endProduction(productionOrderId,dto);
        List<ProductionOrder> productionOrderList = Collections.singletonList(productionOrder);
        return ResponseEntity.ok(new PageImpl<>(productionOrderList, org.springframework.data.domain.PageRequest.of(0, 1), 1));
    }

    @PostMapping("/production/what-is-next-rrn/{production-order-id}")
    public ResponseEntity<Page<ProductionOrder>> productionWhatIsNextRRN(
            @PathVariable("production-order-id") Long productionOrderId,
            @RequestBody ProductionOrderSimulatorRequestDto dto
    ){
        ProductionOrder productionOrder = powderSimulatorFacade.whatIsNextRRNProduction(productionOrderId,dto);
        List<ProductionOrder> productionOrderList = Collections.singletonList(productionOrder);
        return ResponseEntity.ok(new PageImpl<>(productionOrderList, org.springframework.data.domain.PageRequest.of(0, 1), 1));
    }

    @PostMapping("/production/move-rrn/{production-order-id}")
    public ResponseEntity<Page<ProductionOrder>> productionMoveRRN(
            @PathVariable("production-order-id") Long productionOrderId,
            @RequestBody ProductionOrderSimulatorRequestDto dto
    ){
        Optional<ProductionOrder> optionalProductionOrder = productionOrderService.findById(productionOrderId);
        if(optionalProductionOrder.isPresent()){
            ProductionOrder productionOrder = optionalProductionOrder.get();
            List<ProductionOrder> productionOrderList = Collections.singletonList(productionOrder);
            return ResponseEntity.ok(new PageImpl<>(productionOrderList, org.springframework.data.domain.PageRequest.of(0, 1), 1));
        }
        else{
            return ResponseEntity.ok(new PageImpl<>(Collections.emptyList(), org.springframework.data.domain.PageRequest.of(0, 1), 1));
        }

    }

    // MoveRRN : MoveRRN 조업

    @PostMapping("/moverrn/transfer/{h2order-dp-line-id}")
    public ResponseEntity<Page<ProductionOrder>> moveRRNTransfer(
            @PathVariable("h2order-dp-line-id") Long h2orderDpLineId
    ){
        ProductionOrder productionOrder = powderSimulatorFacade.transferMoveRRN(h2orderDpLineId);
        List<ProductionOrder> productionOrderList = Collections.singletonList(productionOrder);
        return ResponseEntity.ok(new PageImpl<>(productionOrderList, org.springframework.data.domain.PageRequest.of(0, 1), 1));
    }

    @PostMapping("/moverrn/accept/{production-order-id}")
    public ResponseEntity<Page<ProductionOrder>> moveRRNAccept(
            @PathVariable("production-order-id") Long productionOrderId
    ){
        ProductionOrder productionOrder = powderSimulatorFacade.acceptMoveRRN(productionOrderId);
        List<ProductionOrder> productionOrderList = Collections.singletonList(productionOrder);
        return ResponseEntity.ok(new PageImpl<>(productionOrderList, org.springframework.data.domain.PageRequest.of(0, 1), 1));
    }

    @PostMapping("/moverrn/missing-qty/{production-order-id}")
    public ResponseEntity<Page<ProductionOrder>> moveRRNMissingQty(
            @PathVariable("production-order-id") Long productionOrderId,
            @RequestBody ProductionOrderSimulatorRequestDto dto
    ){
        ProductionOrder productionOrder = powderSimulatorFacade.missingQtyMoveRRN(productionOrderId,dto);
        List<ProductionOrder> productionOrderList = Collections.singletonList(productionOrder);
        return ResponseEntity.ok(new PageImpl<>(productionOrderList, org.springframework.data.domain.PageRequest.of(0, 1), 1));
    }

    @PostMapping("/moverrn/surplus-qty/{production-order-id}")
    public ResponseEntity<Page<ProductionOrder>> moveRRNSurplusQty(
            @PathVariable("production-order-id") Long productionOrderId,
            @RequestBody ProductionOrderSimulatorRequestDto dto
    ){
        ProductionOrder productionOrder = powderSimulatorFacade.surplusQtyMoveRRN(productionOrderId,dto);
        List<ProductionOrder> productionOrderList = Collections.singletonList(productionOrder);
        return ResponseEntity.ok(new PageImpl<>(productionOrderList, org.springframework.data.domain.PageRequest.of(0, 1), 1));
    }

    @PostMapping("/moverrn/release/{production-order-id}")
    public ResponseEntity<Page<ProductionOrder>> moveRRNRelease(
            @PathVariable("production-order-id") Long productionOrderId
    ){
        ProductionOrder productionOrder = powderSimulatorFacade.releaseMoveRRN(productionOrderId);
        List<ProductionOrder> productionOrderList = Collections.singletonList(productionOrder);
        return ResponseEntity.ok(new PageImpl<>(productionOrderList, org.springframework.data.domain.PageRequest.of(0, 1), 1));
    }

    @PostMapping("/moverrn/container/{production-order-id}")
    public ResponseEntity<Page<ProductionOrder>> moveRRNContainer(
            @PathVariable("production-order-id") Long productionOrderId,
            @RequestBody ProductionOrderSimulatorRequestDto dto
    ){
        ProductionOrder productionOrder = powderSimulatorFacade.containerChangedMoveRRN(productionOrderId,dto);
        List<ProductionOrder> productionOrderList = Collections.singletonList(productionOrder);
        return ResponseEntity.ok(new PageImpl<>(productionOrderList, org.springframework.data.domain.PageRequest.of(0, 1), 1));
    }

    @PostMapping("/moverrn/complete/{production-order-id}")
    public ResponseEntity<Page<ProductionOrder>> moveRRNCompleted(
            @PathVariable("production-order-id") Long productionOrderId,
            @RequestBody ProductionOrderSimulatorRequestDto dto
    ){
        ProductionOrder productionOrder = powderSimulatorFacade.completedMoveRRN(productionOrderId,dto);
        List<ProductionOrder> productionOrderList = Collections.singletonList(productionOrder);
        return ResponseEntity.ok(new PageImpl<>(productionOrderList, org.springframework.data.domain.PageRequest.of(0, 1), 1));
    }

    // EnterToStock : EnterToStock 조업

    @PostMapping("/stock/transfer/{h2order-dp-line-id}")
    public ResponseEntity<Page<ProductionOrder>> enterToStockTransfer(
            @PathVariable("h2order-dp-line-id") Long h2orderDpLineId
    ){
        ProductionOrder productionOrder = powderSimulatorFacade.transferEnterToStock(h2orderDpLineId);
        List<ProductionOrder> productionOrderList = Collections.singletonList(productionOrder);
        return ResponseEntity.ok(new PageImpl<>(productionOrderList, org.springframework.data.domain.PageRequest.of(0, 1), 1));
    }

    @PostMapping("/stock/accept/{production-order-id}")
    public ResponseEntity<Page<ProductionOrder>> enterToStockAccept(
            @PathVariable("production-order-id") Long productionOrderId
    ){
        ProductionOrder productionOrder = powderSimulatorFacade.acceptEnterToStockAccept(productionOrderId);
        List<ProductionOrder> productionOrderList = Collections.singletonList(productionOrder);
        return ResponseEntity.ok(new PageImpl<>(productionOrderList, org.springframework.data.domain.PageRequest.of(0, 1), 1));
    }

    @PostMapping("/stock/release/{production-order-id}")
    public ResponseEntity<Page<ProductionOrder>> enterToStockRelease(
            @PathVariable("production-order-id") Long productionOrderId
    ){
        ProductionOrder productionOrder = powderSimulatorFacade.releaseEnterToStock(productionOrderId);
        List<ProductionOrder> productionOrderList = Collections.singletonList(productionOrder);
        return ResponseEntity.ok(new PageImpl<>(productionOrderList, org.springframework.data.domain.PageRequest.of(0, 1), 1));
    }

    @PostMapping("/stock/container/{production-order-id}")
    public ResponseEntity<Page<ProductionOrder>> enterToStockContainer(
            @PathVariable("production-order-id") Long productionOrderId,
            @RequestBody ProductionOrderSimulatorRequestDto dto
    ){
        ProductionOrder productionOrder = powderSimulatorFacade.containerEnterToStock(productionOrderId,dto);
        List<ProductionOrder> productionOrderList = Collections.singletonList(productionOrder);
        return ResponseEntity.ok(new PageImpl<>(productionOrderList, org.springframework.data.domain.PageRequest.of(0, 1), 1));
    }

    @PostMapping("/stock/missing-qty/{production-order-id}")
    public ResponseEntity<Page<ProductionOrder>> enterToStockMissingQty(
            @PathVariable("production-order-id") Long productionOrderId,
            @RequestBody ProductionOrderSimulatorRequestDto dto
    ){
        ProductionOrder productionOrder = powderSimulatorFacade.missingQtyEnterToStock(productionOrderId,dto);
        List<ProductionOrder> productionOrderList = Collections.singletonList(productionOrder);
        return ResponseEntity.ok(new PageImpl<>(productionOrderList, org.springframework.data.domain.PageRequest.of(0, 1), 1));
    }

    @PostMapping("/stock/complete/{production-order-id}")
    public ResponseEntity<Page<ProductionOrder>> enterToStockComplete(
            @PathVariable("production-order-id") Long productionOrderId
    ){
        ProductionOrder productionOrder = powderSimulatorFacade.orderCompleted(productionOrderId);
        List<ProductionOrder> productionOrderList = Collections.singletonList(productionOrder);
        return ResponseEntity.ok(new PageImpl<>(productionOrderList, org.springframework.data.domain.PageRequest.of(0, 1), 1));
    }




}
