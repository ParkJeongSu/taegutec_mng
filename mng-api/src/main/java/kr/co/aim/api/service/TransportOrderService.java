package kr.co.aim.api.service;

import kr.co.aim.api.vo.insert.sim.H2TransReportVo;
import kr.co.aim.api.vo.insert.sim.TransportOrderContext;
import kr.co.aim.common.condition.TransportOrderSearchCondition;
import kr.co.aim.common.dto.insert.TransportOrderStatisticsResponse;
import kr.co.aim.common.dto.insert.WorkStationTransportCountResponse;
import kr.co.aim.common.enums.EventName;
import kr.co.aim.common.enums.SystemName;
import kr.co.aim.common.enums.TransportOrderStatus;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.TransportOrderCreateCommand;
import kr.co.aim.domain.model.TransportOrder;
import kr.co.aim.domain.repository.TransportOrderRepository;
import kr.co.aim.infra.persistence.db2entity.insert.H2OrderDEntity;
import kr.co.aim.infra.persistence.db2entity.insert.H2OrderMEntity;
import kr.co.aim.infra.persistence.db2entity.insert.IdocEntity;
import kr.co.aim.infra.persistence.entity.TransportOrderHistoryEntity;
import kr.co.aim.infra.persistence.mapper.TransportOrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransportOrderService {

    private final TransportOrderRepository transportOrderRepository;
    private final TransportOrderMapper transportOrderMapper;
    private final HistoryService historyService;

    public TransportOrder createBaseBuilder(TransportOrderContext context) {
        TransactionInfo transactionInfo = TransactionInfo.now(
                EventName.TRANSFER.getValue(),
                SystemName.MNG.getValue(),
                "");
        IdocEntity idoc = context.getIdoc();
        H2OrderMEntity master = context.getMaster();
        List<H2OrderDEntity> details = context.getDetails();
        TransportOrderCreateCommand command =
                TransportOrderCreateCommand
                        .builder()
                        .transactionInfo(transactionInfo)
                        .transportOrderId(master.getCOrderId())
                        .idocId(idoc.getLineId())
                        .description("")
                        .carrierName(details.get(0).getCCoId())
                        .transportType(master.getCOrderTy())
                        .transportStatus(TransportOrderStatus.CREATED.getValue())
                        .lastTransactionCode("")
                        .carrierType(details.get(0).getCCoTy())
                        .priority(master.getCOrderPrio())
                        .galId(master.getCGalId())
                        .galWarehouse(master.getCGalWhs())
                        .locationId(master.getCLocId())
                        .workStationId(master.getCWcId())
                        .sourceZoneName(context.isRelocation() ? details.get(0).getCZone() : details.get(1).getCZone())
                        .destinationZoneName(context.isRelocation() ? details.get(1).getCZone() : details.get(0).getCZone())
                        .requestedZoneName(context.isRelocation() ? details.get(1).getCZone() : details.get(0).getCZone())
                        .travelProfile(details.get(0).getCDrivingProfile())
                        .createTime(transactionInfo.eventTime())
                        .createUser(SystemName.GAL.getValue())
                        .build();

        return TransportOrder.create(command);
    }

    @Transactional(value = "mssqlTransactionManager", propagation = Propagation.REQUIRES_NEW)
    public TransportOrder createTransportOrder(TransportOrder transportOrder) {
        TransportOrder savedTransportOrder = transportOrderRepository.save(transportOrder);
        TransportOrderHistoryEntity historyEntity = transportOrderMapper.toHistoryEntity(savedTransportOrder);
        historyService.saveHistory(historyEntity);
        return savedTransportOrder;
    }

    @Transactional(value = "mssqlTransactionManager", propagation = Propagation.REQUIRES_NEW)
    public TransportOrder acceptTransportOrder(TransportOrder transportOrder) {
        TransactionInfo transactionInfo = TransactionInfo.now(TransportOrderStatus.ACCEPTED.getValue(), SystemName.MNG.getValue(), "");

        transportOrder.setTransportStatus(TransportOrderStatus.ACCEPTED.getValue());
        transportOrder.setEventTime(transactionInfo.eventTime());
        transportOrder.setEventName(transactionInfo.eventName());
        transportOrder.setEventUser(transactionInfo.eventUser());

        TransportOrder savedTransportOrder = transportOrderRepository.save(transportOrder);
        TransportOrderHistoryEntity historyEntity = transportOrderMapper.toHistoryEntity(savedTransportOrder);
        historyService.saveHistory(historyEntity);
        return savedTransportOrder;
    }

    @Transactional("mssqlTransactionManager")
    public TransportOrder registerTransportOrder(TransportOrderContext context) {
        TransportOrder transportOrder = createBaseBuilder(context);
        TransportOrder savedTransportOrder = transportOrderRepository.save(transportOrder);
        TransportOrderHistoryEntity historyEntity = transportOrderMapper.toHistoryEntity(savedTransportOrder);
        historyService.saveHistory(historyEntity);
        return savedTransportOrder;
    }

    @Transactional("mssqlTransactionManager")
    public TransportOrder updateStatusTransportOrder(H2TransReportVo vo) {
        log.info("updateStatusTransportOrder");
        Optional<TransportOrder> optionalTransportOrder = transportOrderRepository.findByTransportOrderId(vo.getOrderId());
        if (optionalTransportOrder.isEmpty()) {
            throw new RuntimeException("TransportOrder를 찾을 수 없습니다. (요청 ID: " + vo.getOrderId() + ")");
        }

        TransportOrder transportOrder = optionalTransportOrder.get();
        transportOrder.setTransportStatus(vo.getStatus().name());
        TransportOrder savedTransportOrder = transportOrderRepository.save(transportOrder);
        TransportOrderHistoryEntity historyEntity = transportOrderMapper.toHistoryEntity(savedTransportOrder);
        historyService.saveHistory(historyEntity);
        return savedTransportOrder;
    }

    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public TransportOrder findById(Long id) {
        Optional<TransportOrder> optional = transportOrderRepository.findById(id);
        if (optional.isEmpty()) {
            throw new IllegalArgumentException("해당 Transport Order 정보가 존재하지 않습니다. ID: " + id);
        }
        return optional.get();
    }

    @Transactional("mssqlTransactionManager")
    public Optional<TransportOrder> findByTransportOrderId(String orderId) {
        return transportOrderRepository.findByTransportOrderId(orderId);
    }

    @Transactional("mssqlTransactionManager")
    public Optional<TransportOrder> findWithLockById(Long id) {
        return transportOrderRepository.findWithLockById(id);
    }

    @Transactional("mssqlTransactionManager")
    public List<TransportOrder> findByTransportTypeInAndTransportStatus(List<String> types, String status) {
        return transportOrderRepository.findByTransportTypeInAndTransportStatus(types, status);
    }

    @Transactional("mssqlTransactionManager")
    public List<TransportOrder> findOutboundOrderForTransportRequest(String transportType, String transportStatus, String workStationId) {
        return transportOrderRepository.findOutboundOrderForTransportRequest(transportType, transportStatus, workStationId);
    }

    @Transactional("mssqlTransactionManager")
    public List<TransportOrder> findOutboundOrderForTransportRequest(
            String transportType,
            List<String> transportStatus,
            String workStationId
    ){
        return transportOrderRepository.findOutboundOrderForTransportRequest(transportType, transportStatus, workStationId);
    }

    @Transactional("mssqlTransactionManager")
    public List<TransportOrder> findTransportOrderByCondition(String carrierName, String transportType, List<String> transportStatus) {
        return transportOrderRepository.findTransportOrderByCondition(carrierName, transportType, transportStatus);
    }

    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public Page<TransportOrder> findTransportOrderWithConditions(TransportOrderSearchCondition condition, Pageable pageable) {
        return transportOrderRepository.findTransportOrderWithConditions(condition, pageable);
    }

    @Transactional("mssqlTransactionManager")
    public TransportOrder save(TransportOrder transportOrder) {
        return transportOrderRepository.save(transportOrder);
    }

    @Transactional("mssqlTransactionManager")
    public Long findMaxOrderId(){
        return  transportOrderRepository.findMaxOrderId();
    }

    @Transactional("mssqlTransactionManager")
    public TransportOrderStatisticsResponse getWorkStationStatistics(String workStationId, LocalDate targetDate) {
        return transportOrderRepository.getWorkStationStatistics(workStationId, targetDate);
    }

    @Transactional("mssqlTransactionManager")
    public Page<TransportOrder> findRecentTransportOrders(String workStationId, String transportType, int limit) {
        return transportOrderRepository.findRecentTransportOrders(workStationId, transportType, limit);
    }

    @Transactional("mssqlTransactionManager")
    public Page<WorkStationTransportCountResponse> getWorkStationTransportCounts(LocalDate targetDate, Pageable pageable) {
        return transportOrderRepository.getWorkStationTransportCounts(targetDate, pageable);
    }


}