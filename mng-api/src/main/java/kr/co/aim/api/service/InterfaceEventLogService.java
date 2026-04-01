package kr.co.aim.api.service;

import kr.co.aim.api.vo.insert.ops.InsertEventLogReportVo;
import kr.co.aim.api.strategy.FactoryProcessStrategy;
import kr.co.aim.domain.model.InterfaceEventLog;
import kr.co.aim.domain.repository.InterfaceEventLogRepository;
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
@Profile({"pex","tex","scheduler"})
public class InterfaceEventLogService {
    private final InterfaceEventLogRepository interfaceEventLogRepository;
    private final FactoryProcessStrategy factoryProcessStrategy;

    @Transactional(value = "mssqlTransactionManager")
    public List<InterfaceEventLog> findByIfStatusOrderByCreateTimeAsc(String ifStatus){
        return interfaceEventLogRepository.findByIfStatusOrderByCreateTimeAsc(ifStatus);
    }

    /*
    * try{
    * InterfaceEventLogService.saveInterfaceEventLog(vo);
    * }
    * catch(Exception e){
    * log.error("로그 저장 실패");
    * }
    * 위 방식으로 호출 해야함
    *
    * */

    @Transactional(value = "mssqlTransactionManager", propagation = Propagation.REQUIRES_NEW) // 독립 트랜잭션 생성
    public void saveInterfaceEventLog(InsertEventLogReportVo vo) {
        factoryProcessStrategy.saveInterfaceEventLog(vo);
    }



}