package kr.co.aim.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.aim.api.dto.ProductionOrderSimulatorRequestDto;
import kr.co.aim.api.vo.powder.sim.H2TransReportVo;
import kr.co.aim.api.vo.powder.sim.ProductionOrderContext;
import kr.co.aim.common.Utils.FormatUtils;
import kr.co.aim.common.Utils.JsonUtils;
import kr.co.aim.common.dto.powder.IdocH2PartMResponseDto;
import kr.co.aim.common.enums.GALProductionStatus;
import kr.co.aim.common.enums.MessageList;
import kr.co.aim.common.enums.SystemName;
import kr.co.aim.common.format.RecipeRequestBody;
import kr.co.aim.common.format.request.BaseMessage;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.ProductDefCreateCommand;
import kr.co.aim.domain.model.ProductDef;
import kr.co.aim.domain.model.ProductionOrder;
import kr.co.aim.infra.config.RabbitConfig;
import kr.co.aim.infra.persistence.db2entity.powder.H2OrderDPEntity;
import kr.co.aim.infra.persistence.db2entity.powder.H2OrderMPEntity;
import kr.co.aim.infra.persistence.db2entity.powder.H2PartMPEntity;
import kr.co.aim.infra.persistence.db2entity.powder.IdocPEntity;
import kr.co.aim.infra.persistence.db2springdatajpa.powder.H2OrderDPJpaRepository;
import kr.co.aim.infra.persistence.db2springdatajpa.powder.H2OrderMPJpaRepository;
import kr.co.aim.infra.persistence.db2springdatajpa.powder.H2PartMPJpaRepository;
import kr.co.aim.infra.persistence.db2springdatajpa.powder.IdocPJpaRepository;
import kr.co.aim.infra.persistence.entity.ProductDefHistoryEntity;
import kr.co.aim.infra.persistence.mapper.ProductDefHistoryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Profile({"simulator"})
@RequiredArgsConstructor
@ConditionalOnProperty(name = "factory.type", havingValue = "powder")
public class PowderSimulatorFacade {

    private final ProductionOrderService productionOrderService;
    private final PowderSimulatorInterfaceService powderSimulatorInterfaceService;
    private final PowderExternalInterfaceService powderExternalInterfaceService;
    private final IdocPJpaRepository idocPJpaRepository;
    private final H2OrderMPJpaRepository h2OrderMPJpaRepository;
    private final H2OrderDPJpaRepository h2OrderDPJpaRepository;
    private final H2PartMPJpaRepository h2PartMPJpaRepository;
    private final ProductDefService productDefService;
    private final RabbitTemplate rabbitTemplate;
    private final JsonUtils jsonUtils;
    private final ObjectMapper objectMapper; // ObjectMapper 선언
    private final HistoryService historyService;
    private final ProductDefHistoryMapper productDefHistoryMapper;


    // 1. IDOC ID 기준으로 전체 컨텍스트 준비 (최초 주문 생성용)
    private ProductionOrderContext prepareByH2orderDpLineIdNotInProductionOrder(Long h2orderDpLineId) {

        Optional<H2OrderDPEntity> optionalH2OrderDPEntity = h2OrderDPJpaRepository.findById(h2orderDpLineId);
        if(optionalH2OrderDPEntity.isEmpty()){
            throw new RuntimeException("h2orderDpLineId를 찾을 수 없습니다. ID:" + h2orderDpLineId);
        }
        H2OrderDPEntity h2OrderDPEntity = optionalH2OrderDPEntity.get();
        Long idocId = h2OrderDPEntity.getIdocId();

        Optional<IdocPEntity> optionalIdocP = idocPJpaRepository.findByLineId(idocId);
        if (optionalIdocP.isEmpty()) {
            throw new RuntimeException("IDOC을 찾을 수 없습니다. ID:" + idocId);
        }
        H2OrderMPEntity h2OrderMPEntity = h2OrderMPJpaRepository.findByIdocId(idocId).get(0);


        return ProductionOrderContext.builder()
                .idoc(optionalIdocP.get())
                .master(h2OrderMPEntity)
                .detail(h2OrderDPEntity)
                .build();
    }

    // 2. h2orderDPLineId 기준으로 전체 컨텍스트 준비 (상태 보고용)
    private ProductionOrderContext prepareByH2orderDpLineId(Long h2orderDpLineId) {
        Optional<ProductionOrder> optionalProductionOrder = productionOrderService.findByH2OrderDpLineId(h2orderDpLineId);
        if(optionalProductionOrder.isEmpty()){
            throw new RuntimeException("ProductionOrder를 찾을 수 없습니다. (요청 h2orderDPLineId: " + h2orderDpLineId + ")");
        }
        ProductionOrder productionOrder = optionalProductionOrder.get();

        Optional<H2OrderDPEntity> optionalH2OrderDPEntity = h2OrderDPJpaRepository.findById(h2orderDpLineId);
        if(optionalH2OrderDPEntity.isEmpty()){
            throw new RuntimeException("h2orderDpLineId를 찾을 수 없습니다. ID:" + h2orderDpLineId);
        }
        H2OrderDPEntity h2OrderDPEntity = optionalH2OrderDPEntity.get();
        Long idocId = h2OrderDPEntity.getIdocId();

        Optional<IdocPEntity> optionalIdocP = idocPJpaRepository.findByLineId(idocId);
        if (optionalIdocP.isEmpty()) {
            throw new RuntimeException("IDOC을 찾을 수 없습니다. ID:" + idocId);
        }
        H2OrderMPEntity h2OrderMPEntity = h2OrderMPJpaRepository.findByIdocId(idocId).get(0);

        return ProductionOrderContext.builder()
                .productionOrder(productionOrder)
                .idoc(optionalIdocP.get())
                .master(h2OrderMPEntity)
                .detail(h2OrderDPEntity)
                .build();
    }

    // 2. h2orderDPLineId 기준으로 전체 컨텍스트 준비 (상태 보고용)
    private ProductionOrderContext prepareByIdocIdForH2Part(Long idocId) {
        Optional<IdocPEntity> optionalIdocP = idocPJpaRepository.findByLineId(idocId);
        if (optionalIdocP.isEmpty()) {
            throw new RuntimeException("IDOC을 찾을 수 없습니다. ID:" + idocId);
        }
        List<H2PartMPEntity> partMPEntityList = h2PartMPJpaRepository.findByIdocId(idocId);

        return ProductionOrderContext.builder()
                .idoc(optionalIdocP.get())
                .partList(partMPEntityList)
                .build();
    }

    // 2. h2orderDPLineId 기준으로 전체 컨텍스트 준비 (상태 보고용)
    private ProductionOrderContext prepareByProductionOrderId(Long productionOrderId, ProductionOrderSimulatorRequestDto dto) {

        Optional<ProductionOrder> optionalProductionOrder = productionOrderService.findById(productionOrderId);
        if(optionalProductionOrder.isEmpty()){
            throw new RuntimeException("ProductionOrder를 찾을 수 없습니다. (요청 productionOrderId: " + productionOrderId + ")");
        }
        ProductionOrder productionOrder = optionalProductionOrder.get();
        Long h2orderDpLineId = productionOrder.getH2OrderDpLineId();

        Optional<H2OrderDPEntity> optionalH2OrderDPEntity = h2OrderDPJpaRepository.findById(h2orderDpLineId);
        if(optionalH2OrderDPEntity.isEmpty()){
            throw new RuntimeException("h2orderDpLineId를 찾을 수 없습니다. ID:" + h2orderDpLineId);
        }
        H2OrderDPEntity h2OrderDPEntity = optionalH2OrderDPEntity.get();
        Long idocId = h2OrderDPEntity.getIdocId();

        Optional<IdocPEntity> optionalIdocP = idocPJpaRepository.findByLineId(idocId);
        if (optionalIdocP.isEmpty()) {
            throw new RuntimeException("IDOC을 찾을 수 없습니다. ID:" + idocId);
        }
        H2OrderMPEntity h2OrderMPEntity = h2OrderMPJpaRepository.findByIdocId(idocId).get(0);

        String lotName = null;
        String itemName = null;
        String carrierName = null;
        String equipmentName = null;
        BigDecimal actualQuantity = null;
        BigDecimal planQuantity = null;
        BigDecimal releasedQuantity = null;
        BigDecimal startedQuantity = null;
        BigDecimal endedQuantity = null;
        BigDecimal scrappedQuantity = null;
        BigDecimal missingQuantity = null;
        BigDecimal surplusQuantity = null;;

        if(ObjectUtils.isNotEmpty(dto)){
            lotName = dto.getLotName();
            itemName = dto.getItemName();
            carrierName = dto.getCarrierName();
            equipmentName = dto.getEquipmentName();
            actualQuantity = dto.getActualQuantity();
            planQuantity = dto.getPlanQuantity();
            releasedQuantity = dto.getReleasedQuantity();
            startedQuantity = dto.getStartedQuantity();
            endedQuantity = dto.getEndedQuantity();
            scrappedQuantity = dto.getScrappedQuantity();
            missingQuantity = dto.getMissingQuantity();
            surplusQuantity = dto.getSurplusQuantity();
        }

        return ProductionOrderContext.builder()
                .productionOrder(productionOrder)
                .idoc(optionalIdocP.get())
                .master(h2OrderMPEntity)
                .detail(h2OrderDPEntity)
                .lotName(lotName)
                .itemName(itemName)
                .carrierName(carrierName)
                .equipmentName(equipmentName)
                .actualQuantity(actualQuantity)
                .planQuantity(planQuantity)
                .releasedQuantity(releasedQuantity)
                .startedQuantity(startedQuantity)
                .endedQuantity(endedQuantity)
                .scrappedQuantity(scrappedQuantity)
                .missingQuantity(missingQuantity)
                .surplusQuantity(surplusQuantity)
                .build();
    }

    private void validateReportState(ProductionOrder productionOrder, GALProductionStatus status) {
        if(ObjectUtils.isNotEmpty(status)){
            if(!StringUtils.equals(productionOrder.getReportState(),status.getValue())){
                throw new RuntimeException("report State != status.");
            }
        }
    }

    public ProductionOrder transferInbound(Long h2orderDpLineId) {
        ProductionOrderContext ctx = prepareByH2orderDpLineIdNotInProductionOrder(h2orderDpLineId);
        ProductionOrder result = productionOrderService.registerProductionOrder(ctx);
        powderSimulatorInterfaceService.transfer(ctx.getIdoc().getLineId());
        return result;
    }

    public ProductionOrder transferProduction(Long h2orderDpLineId) {
        ProductionOrderContext ctx = prepareByH2orderDpLineIdNotInProductionOrder(h2orderDpLineId);
        ProductionOrder result = productionOrderService.registerProductionOrder(ctx);
        powderSimulatorInterfaceService.transfer(ctx.getIdoc().getLineId());
        return result;
    }

    public ProductionOrder transferPacking(Long h2orderDpLineId) {
        ProductionOrderContext ctx = prepareByH2orderDpLineIdNotInProductionOrder(h2orderDpLineId);
        ProductionOrder result = productionOrderService.registerProductionOrder(ctx);
        powderSimulatorInterfaceService.transfer(ctx.getIdoc().getLineId());
        return result;
    }

    public ProductionOrder transferMoveRRN(Long h2orderDpLineId) {
        ProductionOrderContext ctx = prepareByH2orderDpLineIdNotInProductionOrder(h2orderDpLineId);
        ProductionOrder result = productionOrderService.registerProductionOrder(ctx);
        powderSimulatorInterfaceService.transfer(ctx.getIdoc().getLineId());
        return result;
    }

    public ProductionOrder transferUnpacker(Long h2orderDpLineId) {
        ProductionOrderContext ctx = prepareByH2orderDpLineIdNotInProductionOrder(h2orderDpLineId);
        ProductionOrder result = productionOrderService.registerProductionOrder(ctx);
        powderSimulatorInterfaceService.transfer(ctx.getIdoc().getLineId());
        return result;
    }

    public ProductionOrder transferEnterToStock(Long h2orderDpLineId) {
        ProductionOrderContext ctx = prepareByH2orderDpLineIdNotInProductionOrder(h2orderDpLineId);
        ProductionOrder result = productionOrderService.registerProductionOrder(ctx);
        powderSimulatorInterfaceService.transfer(ctx.getIdoc().getLineId());
        return result;
    }

    public ProductionOrder transferIssue(Long h2orderDpLineId) {
        ProductionOrderContext ctx = prepareByH2orderDpLineIdNotInProductionOrder(h2orderDpLineId);
        ProductionOrder result = productionOrderService.registerProductionOrder(ctx);
        powderSimulatorInterfaceService.transfer(ctx.getIdoc().getLineId());
        return result;
    }

    public ProductionOrder acceptInbound(Long productionOrderId) {
        ProductionOrderContext ctx = prepareByProductionOrderId(productionOrderId,null);
        powderSimulatorInterfaceService.accept(ctx);
        H2TransReportVo vo = H2TransReportVo
                .builder()
                .productionOrderId(ctx.getProductionOrder().getId())
                .h2OrderDpLineId(ctx.getProductionOrder().getH2OrderDpLineId())
                .orderId(ctx.getDetail().getCOrderId())
                .status(GALProductionStatus.ACCEPT)
                .build();
        return productionOrderService.updateStatusProductionOrder(vo);
    }

    public ProductionOrder acceptProduction(Long productionOrderId) {
        ProductionOrderContext ctx = prepareByProductionOrderId(productionOrderId,null);
        powderSimulatorInterfaceService.accept(ctx);
        H2TransReportVo vo = H2TransReportVo
                .builder()
                .productionOrderId(ctx.getProductionOrder().getId())
                .h2OrderDpLineId(ctx.getProductionOrder().getH2OrderDpLineId())
                .orderId(ctx.getDetail().getCOrderId())
                .status(GALProductionStatus.ACCEPT)
                .build();
        return productionOrderService.updateStatusProductionOrder(vo);
    }

    public ProductionOrder acceptPacking(Long productionOrderId) {
        ProductionOrderContext ctx = prepareByProductionOrderId(productionOrderId,null);
        powderSimulatorInterfaceService.accept(ctx);
        H2TransReportVo vo = H2TransReportVo
                .builder()
                .productionOrderId(ctx.getProductionOrder().getId())
                .h2OrderDpLineId(ctx.getProductionOrder().getH2OrderDpLineId())
                .orderId(ctx.getDetail().getCOrderId())
                .status(GALProductionStatus.ACCEPT)
                .build();
        return productionOrderService.updateStatusProductionOrder(vo);
    }

    public ProductionOrder acceptEnterToStock(Long productionOrderId) {
        ProductionOrderContext ctx = prepareByProductionOrderId(productionOrderId,null);
        powderSimulatorInterfaceService.accept(ctx);
        H2TransReportVo vo = H2TransReportVo
                .builder()
                .productionOrderId(ctx.getProductionOrder().getId())
                .h2OrderDpLineId(ctx.getProductionOrder().getH2OrderDpLineId())
                .orderId(ctx.getDetail().getCOrderId())
                .status(GALProductionStatus.ACCEPT)
                .build();
        return productionOrderService.updateStatusProductionOrder(vo);
    }

    public ProductionOrder acceptIssue(Long productionOrderId) {
        ProductionOrderContext ctx = prepareByProductionOrderId(productionOrderId,null);
        powderSimulatorInterfaceService.accept(ctx);
        H2TransReportVo vo = H2TransReportVo
                .builder()
                .productionOrderId(ctx.getProductionOrder().getId())
                .h2OrderDpLineId(ctx.getProductionOrder().getH2OrderDpLineId())
                .orderId(ctx.getDetail().getCOrderId())
                .status(GALProductionStatus.ACCEPT)
                .build();
        return productionOrderService.updateStatusProductionOrder(vo);
    }

    public ProductionOrder acceptMoveRRN(Long productionOrderId) {
        ProductionOrderContext ctx = prepareByProductionOrderId(productionOrderId,null);
        powderSimulatorInterfaceService.accept(ctx);
        H2TransReportVo vo = H2TransReportVo
                .builder()
                .productionOrderId(ctx.getProductionOrder().getId())
                .h2OrderDpLineId(ctx.getProductionOrder().getH2OrderDpLineId())
                .orderId(ctx.getDetail().getCOrderId())
                .status(GALProductionStatus.ACCEPT)
                .build();
        return productionOrderService.updateStatusProductionOrder(vo);
    }

    public ProductionOrder acceptUnpacker(Long productionOrderId) {
        ProductionOrderContext ctx = prepareByProductionOrderId(productionOrderId,null);
        powderSimulatorInterfaceService.accept(ctx);
        H2TransReportVo vo = H2TransReportVo
                .builder()
                .productionOrderId(ctx.getProductionOrder().getId())
                .h2OrderDpLineId(ctx.getProductionOrder().getH2OrderDpLineId())
                .orderId(ctx.getDetail().getCOrderId())
                .status(GALProductionStatus.ACCEPT)
                .build();
        return productionOrderService.updateStatusProductionOrder(vo);
    }

    public ProductionOrder releaseInbound(Long productionOrderId) {
        ProductionOrderContext ctx = prepareByProductionOrderId(productionOrderId,null);
        powderSimulatorInterfaceService.release(ctx);
        H2TransReportVo vo = H2TransReportVo
                .builder()
                .productionOrderId(ctx.getProductionOrder().getId())
                .h2OrderDpLineId(ctx.getProductionOrder().getH2OrderDpLineId())
                .orderId(ctx.getDetail().getCOrderId())
                .status(GALProductionStatus.RELEASE)
                .build();
        return productionOrderService.updateStatusProductionOrder(vo);
    }

    public ProductionOrder releaseProduction(Long productionOrderId) {
        ProductionOrderContext ctx = prepareByProductionOrderId(productionOrderId,null);
        powderSimulatorInterfaceService.release(ctx);
        H2TransReportVo vo = H2TransReportVo
                .builder()
                .productionOrderId(ctx.getProductionOrder().getId())
                .h2OrderDpLineId(ctx.getProductionOrder().getH2OrderDpLineId())
                .orderId(ctx.getDetail().getCOrderId())
                .status(GALProductionStatus.RELEASE)
                .build();
        return productionOrderService.updateStatusProductionOrder(vo);
    }

    public ProductionOrder releasePacking(Long productionOrderId) {
        ProductionOrderContext ctx = prepareByProductionOrderId(productionOrderId,null);
        powderSimulatorInterfaceService.release(ctx);
        H2TransReportVo vo = H2TransReportVo
                .builder()
                .productionOrderId(ctx.getProductionOrder().getId())
                .h2OrderDpLineId(ctx.getProductionOrder().getH2OrderDpLineId())
                .orderId(ctx.getDetail().getCOrderId())
                .status(GALProductionStatus.RELEASE)
                .build();
        return productionOrderService.updateStatusProductionOrder(vo);
    }

    public ProductionOrder releaseEnterToStock(Long productionOrderId) {
        ProductionOrderContext ctx = prepareByProductionOrderId(productionOrderId,null);
        powderSimulatorInterfaceService.release(ctx);
        H2TransReportVo vo = H2TransReportVo
                .builder()
                .productionOrderId(ctx.getProductionOrder().getId())
                .h2OrderDpLineId(ctx.getProductionOrder().getH2OrderDpLineId())
                .orderId(ctx.getDetail().getCOrderId())
                .status(GALProductionStatus.RELEASE)
                .build();
        return productionOrderService.updateStatusProductionOrder(vo);
    }

    public ProductionOrder releaseIssue(Long productionOrderId) {
        ProductionOrderContext ctx = prepareByProductionOrderId(productionOrderId,null);
        powderSimulatorInterfaceService.release(ctx);
        H2TransReportVo vo = H2TransReportVo
                .builder()
                .productionOrderId(ctx.getProductionOrder().getId())
                .h2OrderDpLineId(ctx.getProductionOrder().getH2OrderDpLineId())
                .orderId(ctx.getDetail().getCOrderId())
                .status(GALProductionStatus.RELEASE)
                .build();
        return productionOrderService.updateStatusProductionOrder(vo);
    }

    public ProductionOrder releaseMoveRRN(Long productionOrderId) {
        ProductionOrderContext ctx = prepareByProductionOrderId(productionOrderId,null);
        powderSimulatorInterfaceService.release(ctx);
        H2TransReportVo vo = H2TransReportVo
                .builder()
                .productionOrderId(ctx.getProductionOrder().getId())
                .h2OrderDpLineId(ctx.getProductionOrder().getH2OrderDpLineId())
                .orderId(ctx.getDetail().getCOrderId())
                .status(GALProductionStatus.RELEASE)
                .build();
        return productionOrderService.updateStatusProductionOrder(vo);
    }

    public ProductionOrder releaseUnpacker(Long productionOrderId) {
        ProductionOrderContext ctx = prepareByProductionOrderId(productionOrderId,null);
        powderSimulatorInterfaceService.release(ctx);
        H2TransReportVo vo = H2TransReportVo
                .builder()
                .productionOrderId(ctx.getProductionOrder().getId())
                .h2OrderDpLineId(ctx.getProductionOrder().getH2OrderDpLineId())
                .orderId(ctx.getDetail().getCOrderId())
                .status(GALProductionStatus.RELEASE)
                .build();
        return productionOrderService.updateStatusProductionOrder(vo);
    }

    public ProductionOrder fibcOnPalletInbound(Long productionOrderId, ProductionOrderSimulatorRequestDto dto) {
        ProductionOrderContext ctx = prepareByProductionOrderId(productionOrderId,dto);
        powderSimulatorInterfaceService.fibcOnPallet(ctx);
        H2TransReportVo vo = H2TransReportVo
                .builder()
                .productionOrderId(ctx.getProductionOrder().getId())
                .h2OrderDpLineId(ctx.getProductionOrder().getH2OrderDpLineId())
                .orderId(ctx.getDetail().getCOrderId())
                .status(GALProductionStatus.FIBC_ON_PALLET)
                .actQty(dto.getActualQuantity())
                .build();
        return productionOrderService.updateStatusProductionOrder(vo);
    }

    public ProductionOrder whatIsNextRRNProduction(Long productionOrderId, ProductionOrderSimulatorRequestDto dto) {
        ProductionOrderContext ctx = prepareByProductionOrderId(productionOrderId,dto);
        powderSimulatorInterfaceService.whatIsNextRRN(ctx);
        H2TransReportVo vo = H2TransReportVo
                .builder()
                .productionOrderId(ctx.getProductionOrder().getId())
                .h2OrderDpLineId(ctx.getProductionOrder().getH2OrderDpLineId())
                .orderId(ctx.getDetail().getCOrderId())
                .status(GALProductionStatus.WHAT_IS_NEXT_RRN)
                .actQty(dto.getActualQuantity())
                .build();
        return productionOrderService.updateStatusProductionOrder(vo);
    }

    public ProductionOrder startProduction(Long productionOrderId, ProductionOrderSimulatorRequestDto dto) {
        ProductionOrderContext ctx = prepareByProductionOrderId(productionOrderId,dto);
        powderSimulatorInterfaceService.productionStart(ctx);
        H2TransReportVo vo = H2TransReportVo
                .builder()
                .productionOrderId(ctx.getProductionOrder().getId())
                .actQty(ctx.getActualQuantity())
                .h2OrderDpLineId(ctx.getProductionOrder().getH2OrderDpLineId())
                .orderId(ctx.getDetail().getCOrderId())
                .status(GALProductionStatus.PRODUCTION_STARTED)
                .build();
        return productionOrderService.updateStatusProductionOrder(vo);
    }

    public ProductionOrder startPacking(Long productionOrderId, ProductionOrderSimulatorRequestDto dto) {
        ProductionOrderContext ctx = prepareByProductionOrderId(productionOrderId,dto);
        powderSimulatorInterfaceService.packingStart(ctx);
        H2TransReportVo vo = H2TransReportVo
                .builder()
                .productionOrderId(ctx.getProductionOrder().getId())
                .actQty(ctx.getActualQuantity())
                .h2OrderDpLineId(ctx.getProductionOrder().getH2OrderDpLineId())
                .orderId(ctx.getDetail().getCOrderId())
                .status(GALProductionStatus.PACKING_STARTED)
                .build();
        return productionOrderService.updateStatusProductionOrder(vo);
    }

    public ProductionOrder startIssue(Long productionOrderId, ProductionOrderSimulatorRequestDto dto) {
        ProductionOrderContext ctx = prepareByProductionOrderId(productionOrderId,dto);
        powderSimulatorInterfaceService.productionStart(ctx);
        H2TransReportVo vo = H2TransReportVo
                .builder()
                .productionOrderId(ctx.getProductionOrder().getId())
                .actQty(ctx.getActualQuantity())
                .h2OrderDpLineId(ctx.getProductionOrder().getH2OrderDpLineId())
                .orderId(ctx.getDetail().getCOrderId())
                .status(GALProductionStatus.UNPACK_STARTED)
                .build();
        return productionOrderService.updateStatusProductionOrder(vo);
    }

    public ProductionOrder startUnpacker(Long productionOrderId, ProductionOrderSimulatorRequestDto dto) {
        ProductionOrderContext ctx = prepareByProductionOrderId(productionOrderId,dto);
        powderSimulatorInterfaceService.unpackStart(ctx);
        H2TransReportVo vo = H2TransReportVo
                .builder()
                .productionOrderId(ctx.getProductionOrder().getId())
                .actQty(ctx.getActualQuantity())
                .h2OrderDpLineId(ctx.getProductionOrder().getH2OrderDpLineId())
                .orderId(ctx.getDetail().getCOrderId())
                .status(GALProductionStatus.UNPACK_STARTED)
                .build();
        return productionOrderService.updateStatusProductionOrder(vo);
    }

    public ProductionOrder containerEnterToStock(Long productionOrderId, ProductionOrderSimulatorRequestDto dto) {
        ProductionOrderContext ctx = prepareByProductionOrderId(productionOrderId,dto);
        powderSimulatorInterfaceService.changedStockPerContainer(ctx);
        H2TransReportVo vo = H2TransReportVo
                .builder()
                .productionOrderId(ctx.getProductionOrder().getId())
                .actQty(ctx.getActualQuantity())
                .h2OrderDpLineId(ctx.getProductionOrder().getH2OrderDpLineId())
                .orderId(ctx.getDetail().getCOrderId())
                .status(GALProductionStatus.STOCK_CHANGED)
                .build();
        return productionOrderService.updateStatusProductionOrder(vo);
    }

    public ProductionOrder containerChangedMoveRRN(Long productionOrderId, ProductionOrderSimulatorRequestDto dto) {
        ProductionOrderContext ctx = prepareByProductionOrderId(productionOrderId,dto);
        powderSimulatorInterfaceService.reassignRRN(ctx);
        H2TransReportVo vo = H2TransReportVo
                .builder()
                .productionOrderId(ctx.getProductionOrder().getId())
                .actQty(ctx.getActualQuantity())
                .h2OrderDpLineId(ctx.getProductionOrder().getH2OrderDpLineId())
                .orderId(ctx.getDetail().getCOrderId())
                .status(GALProductionStatus.REASSIGN_RRN)
                .build();
        return productionOrderService.updateStatusProductionOrder(vo);
    }

    public ProductionOrder missingQtyEnterToStock(Long productionOrderId, ProductionOrderSimulatorRequestDto dto) {
        ProductionOrderContext ctx = prepareByProductionOrderId(productionOrderId,dto);
        powderSimulatorInterfaceService.shortage(ctx);
        H2TransReportVo vo = H2TransReportVo
                .builder()
                .productionOrderId(ctx.getProductionOrder().getId())
                .actQty(ctx.getActualQuantity())
                .h2OrderDpLineId(ctx.getProductionOrder().getH2OrderDpLineId())
                .orderId(ctx.getDetail().getCOrderId())
                .status(GALProductionStatus.SHORTAGE)
                .build();
        return productionOrderService.updateStatusProductionOrder(vo);
    }

    public ProductionOrder missingQtyMoveRRN(Long productionOrderId, ProductionOrderSimulatorRequestDto dto) {
        ProductionOrderContext ctx = prepareByProductionOrderId(productionOrderId,dto);
        powderSimulatorInterfaceService.missingQty(ctx);
        H2TransReportVo vo = H2TransReportVo
                .builder()
                .productionOrderId(ctx.getProductionOrder().getId())
                .actQty(ctx.getActualQuantity())
                .h2OrderDpLineId(ctx.getProductionOrder().getH2OrderDpLineId())
                .orderId(ctx.getDetail().getCOrderId())
                .status(GALProductionStatus.MISSING_QUANTITY)
                .build();
        return productionOrderService.updateStatusProductionOrder(vo);
    }

    public ProductionOrder surplusQtyMoveRRN(Long productionOrderId, ProductionOrderSimulatorRequestDto dto) {
        ProductionOrderContext ctx = prepareByProductionOrderId(productionOrderId,dto);
        powderSimulatorInterfaceService.surplusQty(ctx);
        H2TransReportVo vo = H2TransReportVo
                .builder()
                .productionOrderId(ctx.getProductionOrder().getId())
                .actQty(ctx.getActualQuantity())
                .h2OrderDpLineId(ctx.getProductionOrder().getH2OrderDpLineId())
                .orderId(ctx.getDetail().getCOrderId())
                .status(GALProductionStatus.SURPLUS_QUANTITY)
                .build();
        return productionOrderService.updateStatusProductionOrder(vo);
    }

    public ProductionOrder endProduction(Long productionOrderId, ProductionOrderSimulatorRequestDto dto) {
        ProductionOrderContext ctx = prepareByProductionOrderId(productionOrderId,dto);
        powderSimulatorInterfaceService.productionEnd(ctx);
        H2TransReportVo vo = H2TransReportVo
                .builder()
                .productionOrderId(ctx.getProductionOrder().getId())
                .actQty(ctx.getActualQuantity())
                .h2OrderDpLineId(ctx.getProductionOrder().getH2OrderDpLineId())
                .orderId(ctx.getDetail().getCOrderId())
                .status(GALProductionStatus.PRODUCTION_ENDED)
                .build();
        return productionOrderService.updateStatusProductionOrder(vo);
    }

    public ProductionOrder endPacking(Long productionOrderId, ProductionOrderSimulatorRequestDto dto) {
        ProductionOrderContext ctx = prepareByProductionOrderId(productionOrderId,dto);
        powderSimulatorInterfaceService.packingEnd(ctx);
        H2TransReportVo vo = H2TransReportVo
                .builder()
                .productionOrderId(ctx.getProductionOrder().getId())
                .actQty(ctx.getActualQuantity())
                .h2OrderDpLineId(ctx.getProductionOrder().getH2OrderDpLineId())
                .orderId(ctx.getDetail().getCOrderId())
                .status(GALProductionStatus.PACKING_ENDED)
                .build();
        return productionOrderService.updateStatusProductionOrder(vo);
    }

    public ProductionOrder endIssue(Long productionOrderId, ProductionOrderSimulatorRequestDto dto) {
        ProductionOrderContext ctx = prepareByProductionOrderId(productionOrderId,dto);
        powderSimulatorInterfaceService.issueEnd(ctx);
        H2TransReportVo vo = H2TransReportVo
                .builder()
                .productionOrderId(ctx.getProductionOrder().getId())
                .actQty(ctx.getActualQuantity())
                .h2OrderDpLineId(ctx.getProductionOrder().getH2OrderDpLineId())
                .orderId(ctx.getDetail().getCOrderId())
                .status(GALProductionStatus.UNPACK_ENDED_STOCK_TO_WIP)
                .build();
        return productionOrderService.updateStatusProductionOrder(vo);
    }

    public ProductionOrder endPackingIssue(Long productionOrderId, ProductionOrderSimulatorRequestDto dto) {
        ProductionOrderContext ctx = prepareByProductionOrderId(productionOrderId,dto);
        powderSimulatorInterfaceService.issueEnd(ctx);
        H2TransReportVo vo = H2TransReportVo
                .builder()
                .productionOrderId(ctx.getProductionOrder().getId())
                .actQty(ctx.getActualQuantity())
                .h2OrderDpLineId(ctx.getProductionOrder().getH2OrderDpLineId())
                .orderId(ctx.getDetail().getCOrderId())
                .status(GALProductionStatus.PRODUCTION_ENDED_STOCK_TO_WIP)
                .build();
        return productionOrderService.updateStatusProductionOrder(vo);
    }

    public ProductionOrder endUnpacker(Long productionOrderId, ProductionOrderSimulatorRequestDto dto) {
        ProductionOrderContext ctx = prepareByProductionOrderId(productionOrderId,dto);
        powderSimulatorInterfaceService.unpackEnd(ctx);
        H2TransReportVo vo = H2TransReportVo
                .builder()
                .productionOrderId(ctx.getProductionOrder().getId())
                .actQty(ctx.getActualQuantity())
                .h2OrderDpLineId(ctx.getProductionOrder().getH2OrderDpLineId())
                .orderId(ctx.getDetail().getCOrderId())
                .status(GALProductionStatus.UNPACK_ENDED)
                .build();
        return productionOrderService.updateStatusProductionOrder(vo);
    }

    public ProductionOrder completedMoveRRN(Long productionOrderId, ProductionOrderSimulatorRequestDto dto) {
        ProductionOrderContext ctx = prepareByProductionOrderId(productionOrderId,dto);
        powderSimulatorInterfaceService.moveRRNCompleted(ctx);
        H2TransReportVo vo = H2TransReportVo
                .builder()
                .productionOrderId(ctx.getProductionOrder().getId())
                .actQty(ctx.getActualQuantity())
                .h2OrderDpLineId(ctx.getProductionOrder().getH2OrderDpLineId())
                .orderId(ctx.getDetail().getCOrderId())
                .status(GALProductionStatus.UNPACK_ENDED)
                .build();
        return productionOrderService.updateStatusProductionOrder(vo);
    }

    public ProductionOrder palletLoadCompletedToWarehouseInbound(Long productionOrderId,ProductionOrderSimulatorRequestDto dto) {
        ProductionOrderContext ctx = prepareByProductionOrderId(productionOrderId,dto);
        powderSimulatorInterfaceService.palletLoadCompletedToWarehouse(ctx);
        H2TransReportVo vo = H2TransReportVo
                .builder()
                .productionOrderId(ctx.getProductionOrder().getId())
                .h2OrderDpLineId(ctx.getProductionOrder().getH2OrderDpLineId())
                .orderId(ctx.getDetail().getCOrderId())
                .status(GALProductionStatus.PALLET_LOAD_COMPLETED_TO_WAREHOUSE)
                .actQty(dto.getActualQuantity())
                .build();
        return productionOrderService.updateStatusProductionOrder(vo);
    }

    public ProductionOrder sendManti(Long productionOrderId,ProductionOrderSimulatorRequestDto dto) {
        ProductionOrderContext ctx = prepareByProductionOrderId(productionOrderId,dto);

        String transactionId = FormatUtils.generateTransactionId();

        BaseMessage<RecipeRequestBody> request = new BaseMessage<>();
        RecipeRequestBody body = new RecipeRequestBody();
        body.setEquipmentName(dto.getEquipmentName());
        body.setPortName(dto.getPortName());
        body.setCarrierName(dto.getCarrierName());
        body.setOrderId(dto.getOrderId());
        body.setOrderLineNumber(dto.getOrderLineNumber());
        body.setTransactionId(dto.getTransactionId());

        request.setEventTime(request.getEventTime());
        request.setMessageFrom(SystemName.MNG.getValue());
        request.setMessageName(MessageList.RECIPE_REQUEST.getMessageName());
        request.setMessageOwner(SystemName.MNG.getValue());
        request.setMessageTo(SystemName.MANTI.getValue());
        request.setResultCode("0");
        request.setResultMessage("0");
        request.setTransactionId(transactionId);
        request.setBody(body);

        try {
            // 3. DTO 객체를 JSON 문자열로 직접 변환합니다.
            String jsonPayload = objectMapper.writeValueAsString(request);
            jsonUtils.writePrettyJson(jsonPayload);
            rabbitTemplate.convertAndSend(
                    RabbitConfig.EXCHANGE_MANTI,
                    RabbitConfig.ROUTING_MANTI,
                    request
            );
        }catch (Exception e){
            e.printStackTrace();
        }
        return ctx.getProductionOrder();
    }


    public ProductionOrder orderLineNoCompletedInbound(Long productionOrderId) {
        ProductionOrderContext ctx = prepareByProductionOrderId(productionOrderId,null);
        powderSimulatorInterfaceService.orderLineNoCompleted(ctx);
        H2TransReportVo vo = H2TransReportVo
                .builder()
                .productionOrderId(ctx.getProductionOrder().getId())
                .h2OrderDpLineId(ctx.getProductionOrder().getH2OrderDpLineId())
                .orderId(ctx.getDetail().getCOrderId())
                .status(GALProductionStatus.ORDER_LINE_NO_COMPLETED)
                .build();
        return productionOrderService.updateStatusProductionOrder(vo);
    }

    public ProductionOrder orderCompleted(Long productionOrderId) {
        ProductionOrderContext ctx = prepareByProductionOrderId(productionOrderId,null);
        powderSimulatorInterfaceService.orderCompleted(ctx);
        H2TransReportVo vo = H2TransReportVo
                .builder()
                .productionOrderId(ctx.getProductionOrder().getId())
                .h2OrderDpLineId(ctx.getProductionOrder().getH2OrderDpLineId())
                .orderId(ctx.getDetail().getCOrderId())
                .status(GALProductionStatus.ORDER_COMPLETED)
                .build();
        return productionOrderService.updateStatusProductionOrder(vo);
    }

    public List<ProductDef> transferH2PartM(Long idocId, Pageable pageable){
        powderSimulatorInterfaceService.transfer(idocId);
        Page<IdocH2PartMResponseDto> idocH2PartMResponseDtoPage = powderExternalInterfaceService.findIdocWithPartMasterByIdocId(idocId,pageable);
        ProductionOrderContext ctx = prepareByIdocIdForH2Part(idocId);
        List<ProductDef> createRequestList = new ArrayList<>();
        List<ProductDef> result = new ArrayList<>();
        TransactionInfo tx = TransactionInfo.now("userTransfer", SystemName.MNG.getValue(), "user Transfer");
        for (IdocH2PartMResponseDto dto : idocH2PartMResponseDtoPage) {

            Optional<ProductDef> optionalProductDef = productDefService.findByH2PartMPEntity(dto.getCPartId());
            ProductionOrderContext pctx =
                    ProductionOrderContext
                            .builder()
                            .idoc(ctx.getIdoc())
                            .partId(dto.getCPartId())
                            .build();
            if(optionalProductDef.isPresent()){
                powderSimulatorInterfaceService.partUpdated(pctx);
                ProductDef productDef = productDefService.update(dto,tx);
                result.add(productDef);
                ProductDefHistoryEntity historyEntity = productDefHistoryMapper.toHistoryEntity(productDef);
                historyService.saveHistory(historyEntity);

            }else{
                powderSimulatorInterfaceService.partCreated(pctx);
                ProductDefCreateCommand command =
                        ProductDefCreateCommand
                                .builder()
                                .productDefName(dto.getCPartId())
                                .factoryName("")
                                .description1(dto.getCPartDsc())
                                .description2(dto.getCPartDsc2())
                                .ratio(dto.getCratIo())
                                .defaultReceiveQuantity(dto.getDefaultReceiveQty())
                                .transactionInfo(tx)
                                .build();
                ProductDef productDef = ProductDef.create(command);
                ProductDefHistoryEntity historyEntity = productDefHistoryMapper.toHistoryEntity(productDef);
                historyService.saveHistory(historyEntity);
                createRequestList.add(productDef);
            }


        }
        result.addAll(productDefService.save(createRequestList));
        return result;
    }

}