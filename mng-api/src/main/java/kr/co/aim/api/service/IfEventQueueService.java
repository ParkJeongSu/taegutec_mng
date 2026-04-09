package kr.co.aim.api.service;

import kr.co.aim.common.enums.IfEventQueueState;
import kr.co.aim.domain.model.IfEventQueue;
import kr.co.aim.domain.repository.IfEventQueueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile({"pex","tex","scheduler"})
public class IfEventQueueService {
    private final IfEventQueueRepository ifEventQueueRepository;
    // private final FactoryProcessStrategy factoryProcessStrategy; << 이걸 주입하면 에러 발생 순환참조
    // IfEventQueueService 는 단순히 eventQueue 에 관한 서비스만

    @Transactional(value = "mssqlTransactionManager", propagation = Propagation.REQUIRES_NEW)
    public List<IfEventQueue> findByIfStatusOrderByCreateTimeAscAndToProcessing(String ifStatus){
        List<IfEventQueue> ifEventQueues = ifEventQueueRepository.findByIfStatusOrderByCreateTimeAsc(ifStatus);
        if(CollectionUtils.isNotEmpty(ifEventQueues)){
            for(IfEventQueue ifEventQueue : ifEventQueues){
                ifEventQueue.setIfStatus(IfEventQueueState.PROCESSING.getValue());
            }
            ifEventQueues = ifEventQueueRepository.save(ifEventQueues);
        }
        return ifEventQueues;
    }

    @Transactional(value = "mssqlTransactionManager", propagation = Propagation.REQUIRES_NEW)
    public List<IfEventQueue> findByIfStatusOrderByCreateTimeAsc(String ifStatus){
        return ifEventQueueRepository.findByIfStatusOrderByCreateTimeAsc(ifStatus);
    }

    @Transactional(value = "mssqlTransactionManager", propagation = Propagation.REQUIRES_NEW) // 독립 트랜잭션 생성
    public void reportCompleted(Long id) {
        // id 조회
        Optional<IfEventQueue> optionalIfEventQueue = ifEventQueueRepository.findById(id);
        if (optionalIfEventQueue.isPresent()) {
            LocalDateTime now = LocalDateTime.now().withNano(0);
            IfEventQueue ifEventQueue = optionalIfEventQueue.get();
            ifEventQueue.setIfStatus(IfEventQueueState.SUCCESS.getValue());
            ifEventQueue.setUpdateTime(now);
            // Success 로 변경
            ifEventQueueRepository.save(ifEventQueue);
        }
    }

    @Transactional(value = "mssqlTransactionManager", propagation = Propagation.REQUIRES_NEW) // 독립 트랜잭션 생성
    public void reportFailed(Long id) {
        // id 조회
        Optional<IfEventQueue> optionalIfEventQueue = ifEventQueueRepository.findById(id);
        if (optionalIfEventQueue.isPresent()) {
            LocalDateTime now = LocalDateTime.now().withNano(0);
            IfEventQueue ifEventQueue = optionalIfEventQueue.get();
            ifEventQueue.setIfStatus(IfEventQueueState.FAIL.getValue());
            ifEventQueue.setUpdateTime(now);
            // Success 로 변경
            ifEventQueueRepository.save(ifEventQueue);
        }
    }

    @Transactional(value = "mssqlTransactionManager", propagation = Propagation.REQUIRES_NEW) // 독립 트랜잭션 생성
    public Optional<IfEventQueue> increaseRetryCnt(Long id) {
        // id 조회
        Optional<IfEventQueue> optionalIfEventQueue = ifEventQueueRepository.findById(id);
        if (optionalIfEventQueue.isPresent()) {
            LocalDateTime now = LocalDateTime.now().withNano(0);
            IfEventQueue ifEventQueue = optionalIfEventQueue.get();
            ifEventQueue.setRetryCNT(ifEventQueue.getRetryCNT() + 1);
            ifEventQueue.setIfStatus(IfEventQueueState.READY.getValue());
            ifEventQueue.setUpdateTime(now);
            // Success 로 변경
            ifEventQueue = ifEventQueueRepository.save(ifEventQueue);
            return Optional.ofNullable(ifEventQueue);
        }
        return Optional.empty();
    }

    @Transactional(value = "mssqlTransactionManager")
    public IfEventQueue save(IfEventQueue ifEventQueue) {
        return ifEventQueueRepository.save(ifEventQueue);
    }




}