package kr.co.aim.api.web.controller;

import kr.co.aim.api.dto.ProductionOrderSimulatorRequestDto;
import kr.co.aim.api.service.*;
import kr.co.aim.common.Utils.FormatUtils;
import kr.co.aim.common.condition.ProductDefSearchCondition;
import kr.co.aim.common.dto.powder.IdocH2PartMResponseDto;
import kr.co.aim.common.dto.powder.IdocH2TransResponseDto;
import kr.co.aim.common.dto.powder.IdocOrderMasterResponseDto;
import kr.co.aim.common.enums.MessageList;
import kr.co.aim.common.enums.ResultCode;
import kr.co.aim.common.enums.SystemName;
import kr.co.aim.common.format.AreYouThereReplyBody;
import kr.co.aim.common.format.RecipeRequestBody;
import kr.co.aim.common.format.request.BaseMessage;
import kr.co.aim.domain.model.ProductDef;
import kr.co.aim.domain.model.ProductionOrder;
import kr.co.aim.infra.persistence.db2entity.powder.H2OrderDPEntity;
import kr.co.aim.infra.persistence.db2entity.powder.H2PartMPEntity;
import kr.co.aim.infra.persistence.db2entity.powder.IdocPEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

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

    private final RabbitTemplate rabbitTemplate;
    private final RestTemplate restTemplate = new RestTemplate();


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
        Page<IdocPEntity> result = powderExternalInterfaceService.findByIdocTypIdWithPartMaster(idocTypId,pageable);
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
        ProductionOrder productionOrder = powderSimulatorFacade.acceptEnterToStock(productionOrderId);
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

    // Issue : Issue

    @PostMapping("/issue/transfer/{h2order-dp-line-id}")
    public ResponseEntity<Page<ProductionOrder>> issueTransfer(
            @PathVariable("h2order-dp-line-id") Long h2orderDpLineId
    ){
        ProductionOrder productionOrder = powderSimulatorFacade.transferIssue(h2orderDpLineId);
        List<ProductionOrder> productionOrderList = Collections.singletonList(productionOrder);
        return ResponseEntity.ok(new PageImpl<>(productionOrderList, org.springframework.data.domain.PageRequest.of(0, 1), 1));
    }

    @PostMapping("/issue/accept/{production-order-id}")
    public ResponseEntity<Page<ProductionOrder>> issueAccept(
            @PathVariable("production-order-id") Long productionOrderId
    ){
        ProductionOrder productionOrder = powderSimulatorFacade.acceptIssue(productionOrderId);
        List<ProductionOrder> productionOrderList = Collections.singletonList(productionOrder);
        return ResponseEntity.ok(new PageImpl<>(productionOrderList, org.springframework.data.domain.PageRequest.of(0, 1), 1));
    }

    @PostMapping("/issue/release/{production-order-id}")
    public ResponseEntity<Page<ProductionOrder>> issueRelease(
            @PathVariable("production-order-id") Long productionOrderId
    ){
        ProductionOrder productionOrder = powderSimulatorFacade.releaseIssue(productionOrderId);
        List<ProductionOrder> productionOrderList = Collections.singletonList(productionOrder);
        return ResponseEntity.ok(new PageImpl<>(productionOrderList, org.springframework.data.domain.PageRequest.of(0, 1), 1));
    }

    @PostMapping("/issue/start/{production-order-id}")
    public ResponseEntity<Page<ProductionOrder>> issueStart(
            @PathVariable("production-order-id") Long productionOrderId,
            @RequestBody ProductionOrderSimulatorRequestDto dto
    ){
        ProductionOrder productionOrder = powderSimulatorFacade.startIssue(productionOrderId,dto);
        List<ProductionOrder> productionOrderList = Collections.singletonList(productionOrder);
        return ResponseEntity.ok(new PageImpl<>(productionOrderList, org.springframework.data.domain.PageRequest.of(0, 1), 1));
    }

    @PostMapping("/issue/end/{production-order-id}")
    public ResponseEntity<Page<ProductionOrder>> issueEnd(
            @PathVariable("production-order-id") Long productionOrderId,
            @RequestBody ProductionOrderSimulatorRequestDto dto
    ){
        ProductionOrder productionOrder = powderSimulatorFacade.endIssue(productionOrderId,dto);
        List<ProductionOrder> productionOrderList = Collections.singletonList(productionOrder);
        return ResponseEntity.ok(new PageImpl<>(productionOrderList, org.springframework.data.domain.PageRequest.of(0, 1), 1));
    }

    // SendManti : SendManti

    @PostMapping("/send-manti/{production-order-id}")
    public ResponseEntity<Page<ProductionOrder>> sendManti(
            @PathVariable("production-order-id") Long productionOrderId,
            @RequestBody ProductionOrderSimulatorRequestDto dto
    ){
        ProductionOrder productionOrder = powderSimulatorFacade.sendManti(productionOrderId,dto);
        List<ProductionOrder> productionOrderList = Collections.singletonList(productionOrder);
        return ResponseEntity.ok(new PageImpl<>(productionOrderList, org.springframework.data.domain.PageRequest.of(0, 1), 1));
    }

    /**
     * 버튼 1 바인딩 엔드포인트: Manti 시스템으로 Recipe Request 메시지 전송
     */
    @PostMapping("/request-recipe")
    public ResponseEntity<String> sendRecipeRequest(@RequestBody ProductionOrderSimulatorRequestDto dto) {
        // Manti 큐와 연계된 운영 Exchange로 메시지 다이렉트 바이패스 발송
        LocalDateTime now = LocalDateTime.now();
        String transactionId = FormatUtils.getTransactionId(now);
        BaseMessage<RecipeRequestBody> request = new BaseMessage<>();
        RecipeRequestBody body = new RecipeRequestBody();
        body.setEquipmentName(dto.getEquipmentName());
        body.setPortName(dto.getPortName());
        body.setCarrierName(dto.getCarrierName());
        body.setOrderId(dto.getOrderId());
        body.setOrderLineNumber(dto.getOrderLineNumber());
        body.setTransactionId(transactionId);

        request.setEventTime(now.toString());
        request.setMessageFrom(SystemName.MNG.getValue());
        request.setMessageName(MessageList.RECIPE_REQUEST.getMessageName());
        request.setMessageOwner(SystemName.MNG.getValue());
        request.setMessageTo(SystemName.MANTI.getValue());
        request.setResultCode(ResultCode.OK.getValue());
        request.setResultMessage("");
        request.setTransactionId(transactionId);
        request.setBody(body);
        rabbitTemplate.convertAndSend("system.direct", "key.manti", request);
        return ResponseEntity.ok("SUCCESS");
    }

    /**
     * 그리드 1 연동 엔드포인트: 요청 큐에서 최신 10건 메시지 Peeking (소멸 없음)
     */
    @GetMapping("/queue/requests")
    public ResponseEntity<List<Object>> getRequestQueueList() {
        List<Object> messages = peekQueueMessages("q.manti.request", 10);
        return ResponseEntity.ok(messages);
    }

    /**
     * 그리드 2 연동 엔드포인트: 회신 응답 큐에서 최신 10건 메시지 Peeking (소멸 없음)
     */
    @GetMapping("/queue/replies")
    public ResponseEntity<List<Object>> getReplyQueueList() {
        List<Object> messages = peekQueueMessages("q.pex.request", 10);
        return ResponseEntity.ok(messages);
    }

    /**
     * RabbitMQ Management HTTP API 공통 호출 모듈화 함수 (람다식 전면 배제 스타일)
     */
    private List<Object> peekQueueMessages(String queueName, int targetCount) {
        List<Object> resultList = new ArrayList<>();

        // 원래 호출하려던 주소 문자열 정형화
        String urlStr = "http://localhost:15672/api/queues/%2F/" + queueName + "/get";

        try {
            // [핵심 변경] String 주소를 java.net.URI 객체로 직접 빌드하여
            // RestTemplate 내부의 자동 더블 인코딩 메커니즘을 완전히 우회시킵니다.
            java.net.URI rabbitMqApiUri = new java.net.URI(urlStr);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBasicAuth("eosuser", "eosuser");

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("count", targetCount);
            requestBody.put("requeue", true);
            requestBody.put("encoding", "auto");
            requestBody.put("ackmode", "ack_requeue_true");

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // 첫 번째 인자로 String이 아닌 랩핑된 URI 객체를 바인딩하여 호출
            ResponseEntity<Object[]> response = restTemplate.exchange(
                    rabbitMqApiUri,
                    HttpMethod.POST,
                    entity,
                    Object[].class
            );

            Object[] responseArray = response.getBody();
            if (responseArray != null) {
                for (int i = 0; i < responseArray.length; i++) {
                    resultList.add(responseArray[i]);
                }
            }
        } catch (Exception e) {
            System.err.println("RabbitMQ [" + queueName + "] 피킹 연동 에러: " + e.getMessage());
        }

        return resultList;
    }

    // Issue : Issue

    @PostMapping("/packing-issue/transfer/{h2order-dp-line-id}")
    public ResponseEntity<Page<ProductionOrder>> packingIssueTransfer(
            @PathVariable("h2order-dp-line-id") Long h2orderDpLineId
    ){
        ProductionOrder productionOrder = powderSimulatorFacade.transferIssue(h2orderDpLineId);
        List<ProductionOrder> productionOrderList = Collections.singletonList(productionOrder);
        return ResponseEntity.ok(new PageImpl<>(productionOrderList, org.springframework.data.domain.PageRequest.of(0, 1), 1));
    }

    @PostMapping("/packing-issue/accept/{production-order-id}")
    public ResponseEntity<Page<ProductionOrder>> packingIssueAccept(
            @PathVariable("production-order-id") Long productionOrderId
    ){
        ProductionOrder productionOrder = powderSimulatorFacade.acceptIssue(productionOrderId);
        List<ProductionOrder> productionOrderList = Collections.singletonList(productionOrder);
        return ResponseEntity.ok(new PageImpl<>(productionOrderList, org.springframework.data.domain.PageRequest.of(0, 1), 1));
    }

    @PostMapping("/packing-issue/release/{production-order-id}")
    public ResponseEntity<Page<ProductionOrder>> packingIssueRelease(
            @PathVariable("production-order-id") Long productionOrderId
    ){
        ProductionOrder productionOrder = powderSimulatorFacade.releaseIssue(productionOrderId);
        List<ProductionOrder> productionOrderList = Collections.singletonList(productionOrder);
        return ResponseEntity.ok(new PageImpl<>(productionOrderList, org.springframework.data.domain.PageRequest.of(0, 1), 1));
    }

    @PostMapping("/packing-issue/end/{production-order-id}")
    public ResponseEntity<Page<ProductionOrder>> packingIssueEnd(
            @PathVariable("production-order-id") Long productionOrderId,
            @RequestBody ProductionOrderSimulatorRequestDto dto
    ){
        ProductionOrder productionOrder = powderSimulatorFacade.endPackingIssue(productionOrderId,dto);
        List<ProductionOrder> productionOrderList = Collections.singletonList(productionOrder);
        return ResponseEntity.ok(new PageImpl<>(productionOrderList, org.springframework.data.domain.PageRequest.of(0, 1), 1));
    }

    // packing : packing 조업

    @PostMapping("/packing/transfer/{h2order-dp-line-id}")
    public ResponseEntity<Page<ProductionOrder>> packingTransfer(
            @PathVariable("h2order-dp-line-id") Long h2orderDpLineId
    ){
        ProductionOrder productionOrder = powderSimulatorFacade.transferPacking(h2orderDpLineId);
        List<ProductionOrder> productionOrderList = Collections.singletonList(productionOrder);
        return ResponseEntity.ok(new PageImpl<>(productionOrderList, org.springframework.data.domain.PageRequest.of(0, 1), 1));
    }

    @PostMapping("/packing/accept/{production-order-id}")
    public ResponseEntity<Page<ProductionOrder>> packingAccept(
            @PathVariable("production-order-id") Long productionOrderId
    ){
        ProductionOrder productionOrder = powderSimulatorFacade.acceptPacking(productionOrderId);
        List<ProductionOrder> productionOrderList = Collections.singletonList(productionOrder);
        return ResponseEntity.ok(new PageImpl<>(productionOrderList, org.springframework.data.domain.PageRequest.of(0, 1), 1));
    }

    @PostMapping("/packing/release/{production-order-id}")
    public ResponseEntity<Page<ProductionOrder>> packingRelease(
            @PathVariable("production-order-id") Long productionOrderId
    ){
        ProductionOrder productionOrder = powderSimulatorFacade.releasePacking(productionOrderId);
        List<ProductionOrder> productionOrderList = Collections.singletonList(productionOrder);
        return ResponseEntity.ok(new PageImpl<>(productionOrderList, org.springframework.data.domain.PageRequest.of(0, 1), 1));
    }

    @PostMapping("/packing/start/{production-order-id}")
    public ResponseEntity<Page<ProductionOrder>> packingStart(
            @PathVariable("production-order-id") Long productionOrderId,
            @RequestBody ProductionOrderSimulatorRequestDto dto
    ){
        ProductionOrder productionOrder = powderSimulatorFacade.startPacking(productionOrderId,dto);
        List<ProductionOrder> productionOrderList = Collections.singletonList(productionOrder);
        return ResponseEntity.ok(new PageImpl<>(productionOrderList, org.springframework.data.domain.PageRequest.of(0, 1), 1));
    }

    @PostMapping("/packing/end/{production-order-id}")
    public ResponseEntity<Page<ProductionOrder>> packingEnd(
            @PathVariable("production-order-id") Long productionOrderId,
            @RequestBody ProductionOrderSimulatorRequestDto dto
    ){
        ProductionOrder productionOrder = powderSimulatorFacade.endPacking(productionOrderId,dto);
        List<ProductionOrder> productionOrderList = Collections.singletonList(productionOrder);
        return ResponseEntity.ok(new PageImpl<>(productionOrderList, org.springframework.data.domain.PageRequest.of(0, 1), 1));
    }





}
