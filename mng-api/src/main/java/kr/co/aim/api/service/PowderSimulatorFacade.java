package kr.co.aim.api.service;

import kr.co.aim.api.dto.ProductionOrderSimulatorRequestDto;
import kr.co.aim.api.vo.powder.sim.H2TransReportVo;
import kr.co.aim.api.vo.powder.sim.ProductionOrderContext;
import kr.co.aim.common.enums.GALProductionStatus;
import kr.co.aim.domain.model.ProductionOrder;
import kr.co.aim.infra.persistence.db2entity.powder.H2OrderDPEntity;
import kr.co.aim.infra.persistence.db2entity.powder.H2OrderMPEntity;
import kr.co.aim.infra.persistence.db2entity.powder.IdocPEntity;
import kr.co.aim.infra.persistence.db2springdatajpa.powder.H2OrderDPJpaRepository;
import kr.co.aim.infra.persistence.db2springdatajpa.powder.H2OrderMPJpaRepository;
import kr.co.aim.infra.persistence.db2springdatajpa.powder.IdocPJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@Slf4j
@Profile({"simulator"})
@RequiredArgsConstructor
@ConditionalOnProperty(name = "factory.type", havingValue = "powder")
public class PowderSimulatorFacade {

    private final ProductionOrderService productionOrderService;
    private final PowderSimulatorInterfaceService powderSimulatorInterfaceService;
    private final IdocPJpaRepository idocPJpaRepository;
    private final H2OrderMPJpaRepository h2OrderMPJpaRepository;
    private final H2OrderDPJpaRepository h2OrderDPJpaRepository;

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

    public ProductionOrder transferUnpacker(Long h2orderDpLineId) {
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
                .status(GALProductionStatus.Accept)
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
                .status(GALProductionStatus.Accept)
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
                .status(GALProductionStatus.Released)
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
                .status(GALProductionStatus.Released)
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
                .status(GALProductionStatus.FibcOnPallet)
                .build();
        return productionOrderService.updateStatusProductionOrder(vo);
    }

    public ProductionOrder startUnpacker(Long productionOrderId, ProductionOrderSimulatorRequestDto dto) {
        ProductionOrderContext ctx = prepareByProductionOrderId(productionOrderId,dto);
        powderSimulatorInterfaceService.productionStart(ctx);
        H2TransReportVo vo = H2TransReportVo
                .builder()
                .productionOrderId(ctx.getProductionOrder().getId())
                .actQty(ctx.getActualQuantity())
                .h2OrderDpLineId(ctx.getProductionOrder().getH2OrderDpLineId())
                .orderId(ctx.getDetail().getCOrderId())
                .status(GALProductionStatus.ProductionStarted)
                .build();
        return productionOrderService.updateStatusProductionOrder(vo);
    }

    public ProductionOrder endUnpacker(Long productionOrderId, ProductionOrderSimulatorRequestDto dto) {
        ProductionOrderContext ctx = prepareByProductionOrderId(productionOrderId,dto);
        powderSimulatorInterfaceService.productionEnd(ctx);
        H2TransReportVo vo = H2TransReportVo
                .builder()
                .productionOrderId(ctx.getProductionOrder().getId())
                .actQty(ctx.getActualQuantity())
                .h2OrderDpLineId(ctx.getProductionOrder().getH2OrderDpLineId())
                .orderId(ctx.getDetail().getCOrderId())
                .status(GALProductionStatus.ProductionEnded)
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
                .status(GALProductionStatus.PalletLoadCompletedToWarehouse)
                .build();
        return productionOrderService.updateStatusProductionOrder(vo);
    }

    public ProductionOrder orderLineNoCompletedInbound(Long productionOrderId) {
        ProductionOrderContext ctx = prepareByProductionOrderId(productionOrderId,null);
        powderSimulatorInterfaceService.orderLineNoCompleted(ctx);
        H2TransReportVo vo = H2TransReportVo
                .builder()
                .productionOrderId(ctx.getProductionOrder().getId())
                .h2OrderDpLineId(ctx.getProductionOrder().getH2OrderDpLineId())
                .orderId(ctx.getDetail().getCOrderId())
                .status(GALProductionStatus.OrderLineNoCompleted)
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
                .status(GALProductionStatus.OrderCompleted)
                .build();
        return productionOrderService.updateStatusProductionOrder(vo);
    }

}