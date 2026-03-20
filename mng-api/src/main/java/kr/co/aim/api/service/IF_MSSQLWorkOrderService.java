package kr.co.aim.api.service;

import kr.co.aim.infra.persistence.entity.IF_WorkOrderEntity;
import kr.co.aim.infra.persistence.springdatajpa.IF_WorkOrderJpaMSSQLRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile({"scheduler"})
public class IF_MSSQLWorkOrderService {

    private final IF_WorkOrderJpaMSSQLRepository mssqlIFWorkOrderJpaRepository;

    @Transactional("mssqlTransactionManager")
    public void transferWorkOrderToMSSQL(kr.co.aim.infra.persistence.db2entity.IF_WorkOrderEntity DB2WorkOrder) {
        log.info("Starting data transfer from DB2 to MSSQL");
        IF_WorkOrderEntity MSSQLWorkOrder = new IF_WorkOrderEntity();
        MSSQLWorkOrder.setWorkOrderName(DB2WorkOrder.getWorkOrderName());
        MSSQLWorkOrder.setDescription(DB2WorkOrder.getDescription());
        MSSQLWorkOrder.setVendorId(DB2WorkOrder.getVendorId());
        MSSQLWorkOrder.setProductDefId(DB2WorkOrder.getProductDefId());
        MSSQLWorkOrder.setProcessFlowId(DB2WorkOrder.getProcessFlowId());
        MSSQLWorkOrder.setProcessOperationId(DB2WorkOrder.getProcessOperationId());
        MSSQLWorkOrder.setEquipmentName(DB2WorkOrder.getEquipmentName());
        MSSQLWorkOrder.setPlanQuantity(DB2WorkOrder.getPlanQuantity());
        MSSQLWorkOrder.setIfState(DB2WorkOrder.getIfState());
        MSSQLWorkOrder.setCreateTime(DB2WorkOrder.getCreateTime());
        MSSQLWorkOrder.setDueDate(DB2WorkOrder.getDueDate());
        MSSQLWorkOrder.setEventName(DB2WorkOrder.getEventName());
        MSSQLWorkOrder.setEventTime(DB2WorkOrder.getEventTime());
        MSSQLWorkOrder.setEventUser(DB2WorkOrder.getEventUser());
        MSSQLWorkOrder.setEventComment(DB2WorkOrder.getEventComment());
        mssqlIFWorkOrderJpaRepository.save(MSSQLWorkOrder);

    }
}