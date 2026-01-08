package kr.co.aim.api.service;

import kr.co.aim.infra.persistence.entitydb2.H2OrderdEntity;
import kr.co.aim.infra.persistence.entitydb2.H2TransEntity;
import kr.co.aim.infra.persistence.entitydb2.IF_WorkOrderEntity;
import kr.co.aim.infra.persistence.entitydb2.IdocEntity;
import kr.co.aim.infra.persistence.springdatajpadb2.H2OrderdRepository;
import kr.co.aim.infra.persistence.springdatajpadb2.H2TransRepository;
import kr.co.aim.infra.persistence.springdatajpadb2.IF_WorkOrderJpaDB2Repository;
import kr.co.aim.infra.persistence.springdatajpadb2.IdocRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile({"scheduler"})
public class DB2WorkOrderService {

    private final IdocRepository idocRepository;
    private final H2OrderdRepository h2OrderdRepository;
    private final H2TransRepository h2TransRepository;


    @Transactional("db2TransactionManager")
    public List<IdocEntity> selectIdocList() {
        log.info("selectIdocList");
        return idocRepository.findByState(1);
    }

    @Transactional("db2TransactionManager")
    public H2OrderdEntity selectH2OrderdByLineId(Long lineId) {
        log.info("selectH2OrderdByLineId");
        return h2OrderdRepository.findBylineId(lineId).orElseThrow();
    }

    @Transactional("db2TransactionManager")
    public H2TransEntity selectH2TransByLineId(Long lineId) {
        log.info("selectH2TransByLineId");
        return h2TransRepository.findBylineId(lineId).orElseThrow();
    }

    // Propagation.REQUIRES_NEW: 항상 새로운 트랜잭션을 시작하도록 강제
    @Transactional(value = "db2TransactionManager", propagation = Propagation.REQUIRES_NEW)
    public void updateDb2StatusToDoneInNewTransaction(Long lineId) {
        IdocEntity idocEntity = idocRepository.findBylineId(lineId).orElseThrow();
        idocEntity.setState(2);
        idocRepository.save(idocEntity);
    }

}