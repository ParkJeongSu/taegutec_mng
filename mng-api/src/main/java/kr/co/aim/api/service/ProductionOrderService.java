package kr.co.aim.api.service;

import kr.co.aim.api.vo.powder.sim.H2TransReportVo;
import kr.co.aim.api.vo.powder.sim.ProductionOrderContext;
import kr.co.aim.common.condition.*;
import kr.co.aim.common.enums.*;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.ProductionOrderCreateCommand;
import kr.co.aim.domain.command.ProductionOrderUpdateStateCommand;
import kr.co.aim.domain.model.*;
import kr.co.aim.domain.repository.ProductionOrderRepository;
import kr.co.aim.infra.persistence.db2entity.powder.H2OrderDPEntity;
import kr.co.aim.infra.persistence.db2entity.powder.H2OrderMPEntity;
import kr.co.aim.infra.persistence.db2entity.powder.IdocPEntity;
import kr.co.aim.infra.persistence.entity.ProductionOrderHistoryEntity;
import kr.co.aim.infra.persistence.mapper.ProductionOrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile({"scheduler","web","pex","tex","simulator"})
public class ProductionOrderService {
    private final ProductionOrderRepository productionOrderRepository;
    private final ProductionOrderMapper productionOrderMapper;
    private final HistoryService historyService;

    public ProductionOrder createBaseBuilder(ProductionOrderContext context) {
        TransactionInfo transactionInfo = TransactionInfo.now(
                EventName.TRANSFER.getValue(),
                SystemName.MNG.getValue(),
                "");
        String productionOrderType = "";
        if(context.getIdoc().getIdocTypId() == 12L){
            productionOrderType = ProductionOrderType.MATERIAL_INBOUND.getValue();
        }else if(context.getIdoc().getIdocTypId() == 13L){
            productionOrderType = ProductionOrderType.UNPACKING.getValue();
        }
        IdocPEntity idoc = context.getIdoc();
        H2OrderMPEntity master = context.getMaster();
        H2OrderDPEntity detail = context.getDetail();

        String orderLineNumber = ObjectUtils.isNotEmpty(detail.getRrn()) ? detail.getRrn().toString() : "";
        String lotName = detail.getLot()== null ? "" : detail.getLot().toString();

        ProductionOrderCreateCommand command =
                ProductionOrderCreateCommand
                        .builder()
                        .transactionInfo(transactionInfo)
                        //.id()
                        .orderId(detail.getCOrderId())
                        .orderLineNumber(orderLineNumber)
                        .lotName(lotName)
                        //.description()
                        .itemName(detail.getCPartId())
                        //.recipeName()
                        //.carrierName()
                        .idocId(detail.getIdocId())
                        .h2OrderDpLineId(detail.getLineId())
                        .galKey(detail.getGalKey())
                        .productionOrderType(productionOrderType)
                        .productionOrderState(ProductionOrderState.CREATED.getValue())
                        .holdState(HoldState.NOT_ON_HOLD.getValue())
                        //.reasonCode()
                        .equipmentName(detail.getMachine())
                        .planQuantity(detail.getQty())
                        //.releasedQuantity()
                        //.startedQuantity()
                        //.endedQuantity()
                        //.scrappedQuantity()
                        .createTime(transactionInfo.eventTime())
                        //.releaseTime()
                        //.completeTime()
                        //.validationTime()
                        .createUser(SystemName.MNG.getValue())
                        //.releaseUser()
                        //.completeUser()
                        //.dueDate()
                        .eventName(transactionInfo.eventName())
                        .eventTime(transactionInfo.eventTime())
                        .eventUser(transactionInfo.eventUser())
                        .eventComment(transactionInfo.eventComment())
                        .build();

        return ProductionOrder.create(command);
    }

    @Transactional(value = "mssqlTransactionManager",propagation = Propagation.REQUIRES_NEW)
    public ProductionOrder createProductionOrder(ProductionOrder productionOrder) {
        ProductionOrder savedProductionOrder = productionOrderRepository.save(productionOrder);
        ProductionOrderHistoryEntity historyEntity = productionOrderMapper.toHistoryEntity(savedProductionOrder);
        historyService.saveHistory(historyEntity);
        return savedProductionOrder;
    }

    @Transactional("mssqlTransactionManager")
    public Optional<ProductionOrder> updateOrderState(TransactionInfo tx ,Long productionOrderId, String orderState){
        Optional<ProductionOrder> optionalProductionOrder = productionOrderRepository.findById(productionOrderId);
        if(optionalProductionOrder.isPresent()){
            ProductionOrder productionOrder = optionalProductionOrder.get();
            ProductionOrderUpdateStateCommand command =
                    ProductionOrderUpdateStateCommand
                            .builder()
                            .transactionInfo(tx)
                            .productionOrderState(orderState)
                            .build();
            productionOrder.updateState(command);
            productionOrder = productionOrderRepository.save(productionOrder);
            ProductionOrderHistoryEntity historyEntity = productionOrderMapper.toHistoryEntity(productionOrder);
            historyService.saveHistory(historyEntity);
            return Optional.of(productionOrder);
        }
        return Optional.empty();
    }

    @Transactional("mssqlTransactionManager")
    public ProductionOrder registerProductionOrder(ProductionOrderContext context) {
        ProductionOrder productionOrder = createBaseBuilder(context);
        ProductionOrder savedProductionOrder = productionOrderRepository.save(productionOrder);
        ProductionOrderHistoryEntity historyEntity = productionOrderMapper.toHistoryEntity(savedProductionOrder);
        historyService.saveHistory(historyEntity);
        return savedProductionOrder;
    }

    @Transactional("mssqlTransactionManager")
    public ProductionOrder updateStatusProductionOrder(H2TransReportVo vo) {
        log.info("updateStatusProductionOrder");
        Optional<ProductionOrder> optionalProductionOrder = productionOrderRepository.findByH2OrderDpLineId(vo.getH2OrderDpLineId());
        if(optionalProductionOrder.isEmpty()){
            throw new RuntimeException("ProductionOrder를 찾을 수 없습니다. (요청 ID: " + vo.getH2OrderDpLineId() + ")");
        }

        ProductionOrder productionOrder = optionalProductionOrder.get();
        // 1. NullPointerException 방지를 위한 실적 수량 안전장치 처리
        BigDecimal actQty = vo.getActQty();
        if (actQty == null) {
            actQty = BigDecimal.ZERO;
        }

        if(vo.getStatus() == GALProductionStatus.UNPACK_STARTED){
            BigDecimal startQuantity = productionOrder.getStartedQuantity();
            if(startQuantity == null){
                // 2. new 연산자 배제하고 전역 캐싱 상수 활용
                startQuantity = BigDecimal.ZERO;
            }
            BigDecimal resultQuantity = startQuantity.add(actQty);
            productionOrder.setStartedQuantity(resultQuantity);
        }
        else if(vo.getStatus() == GALProductionStatus.UNPACK_ENDED){
            BigDecimal endQuantity = productionOrder.getEndedQuantity();
            if(endQuantity == null){
                endQuantity = BigDecimal.ZERO;
            }
            BigDecimal resultQuantity = endQuantity.add(actQty);

            // 3. 버그 수정: setStartedQuantity에서 setEndedQuantity로 정정
            productionOrder.setEndedQuantity(resultQuantity);
        }
        productionOrder.setReportState( vo.getStatus().name());
        ProductionOrder savedProductionOrder = productionOrderRepository.save(productionOrder);
        ProductionOrderHistoryEntity historyEntity = productionOrderMapper.toHistoryEntity(savedProductionOrder);
        historyService.saveHistory(historyEntity);
        return savedProductionOrder;
    }

    @Transactional(readOnly = true)
    public List<ProductionOrder> findActiveProductionOrderList(String equipmentName) {
        List<String> productionOrderStateList = new ArrayList<>();
        productionOrderStateList.add(ProductionOrderState.REQUESTED.getValue());
        productionOrderStateList.add(ProductionOrderState.RELEASED.getValue());
        return productionOrderRepository.findByEquipmentNameAndProductionOrderStateInOrderByCreateTimeAsc(
                equipmentName,
                productionOrderStateList
        );
    }

    @Transactional(readOnly = true)
    public List<ProductionOrder> findByProductionOrderStateInOrderByCreateTimeAsc(
            List<String> productionOrderState
    ){
       return  productionOrderRepository.findByProductionOrderStateInOrderByCreateTimeAsc(productionOrderState);
    }


    @Transactional(readOnly = true)
    public List<ProductionOrder> findNewProductionOrderList(String equipmentName) {
        List<String> productionOrderStateList = new ArrayList<>();
        productionOrderStateList.add(ProductionOrderState.CREATED.getValue());
        return productionOrderRepository.findByEquipmentNameAndProductionOrderStateInOrderByCreateTimeAsc(
                equipmentName,
                productionOrderStateList
        );
    }

    @Transactional(readOnly = true)
    public Page<ProductionOrderSummary> findProductionOrderSummaryByCondition(ProductionOrderSummarySearchCondition condition, Pageable pageable) {
        return productionOrderRepository.findProductionOrderSummaryByCondition(condition,pageable);
    }

    @Transactional(readOnly = true)
    public Page<ProductionOrder> findProductionOrderByCondition(ProductionOrderSearchCondition condition, Pageable pageable) {
        return productionOrderRepository.findProductionOrderByCondition(condition,pageable);
    }

    @Transactional(readOnly = true)
    public Page<ProductionOrderHistory> findProductionOrderHistoryByCondition(ProductionOrderHistorySearchCondition condition, Pageable pageable) {
        return productionOrderRepository.findProductionOrderHistoryByCondition(condition,pageable);
    }

    @Transactional(readOnly = true)
    public List<ProductionOrder> findByCreateTimeBetween(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return productionOrderRepository.findByCreateTimeBetween(startDateTime, endDateTime);
    }

    @Transactional(readOnly = true)
    public List<ProductionOrder> findByCreateTimeBetweenAndProductionOrderState(LocalDateTime startDateTime,
                                                         LocalDateTime endDateTime,
                                                         String productionOrderState) {
        return productionOrderRepository.findByCreateTimeBetweenAndProductionOrderState(startDateTime,
                endDateTime,
                productionOrderState);
    }

    @Transactional(readOnly = true)
    public Optional<ProductionOrder> findByH2OrderDpLineId(Long h2orderDPLineId) {
        return productionOrderRepository.findByH2OrderDpLineId(h2orderDPLineId);
    }

    public Optional<ProductionOrder> findById(Long id){
        return productionOrderRepository.findById(id);
    }


}