package kr.co.aim.api.service;

import kr.co.aim.infra.persistence.db2entity.IF_WorkOrderEntity;
import kr.co.aim.infra.persistence.db2springdatajpa.IF_WorkOrderJpaDB2Repository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile({"scheduler"})
public class IF_DB2WorkOrderService {

    private final IF_WorkOrderJpaDB2Repository db2IFWorkOrderJpaRepository;


    @Transactional("db2TransactionManager")
    public List<IF_WorkOrderEntity> selectAndMarkAsProcessing() {
        log.info("Starting data transfer from DB2 to MSSQL");
        List<IF_WorkOrderEntity> db2WorkOrderEntityList = db2IFWorkOrderJpaRepository.findByifState("Created");
        for(IF_WorkOrderEntity DB2WorkOrder : db2WorkOrderEntityList)
        {
            DB2WorkOrder.setIfState("PROCESSING");
        }
        return db2IFWorkOrderJpaRepository.saveAll(db2WorkOrderEntityList);
    }
    // Propagation.REQUIRES_NEW: 항상 새로운 트랜잭션을 시작하도록 강제
    @Transactional(value = "db2TransactionManager", propagation = Propagation.REQUIRES_NEW)
    public void updateDb2StatusToDoneInNewTransaction(Long id) {
        IF_WorkOrderEntity data = db2IFWorkOrderJpaRepository.findById(id).orElseThrow();
        data.setIfState("DONE");
        db2IFWorkOrderJpaRepository.save(data);
    }

    @Transactional(value = "db2TransactionManager", propagation = Propagation.REQUIRES_NEW)
    public void updateDb2StatusToErrorInNewTransaction(Long id) {
        IF_WorkOrderEntity data = db2IFWorkOrderJpaRepository.findById(id).orElseThrow();
        data.setIfState("Created");
        db2IFWorkOrderJpaRepository.save(data);
    }
}