package kr.co.aim.api.service;

import kr.co.aim.domain.model.LotCarrierMapping;
import kr.co.aim.domain.model.LotCarrierMappingHistory;
import kr.co.aim.domain.repository.LotCarrierMappingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LotCarrierMappingService {

    private final LotCarrierMappingRepository lotCarrierMappingRepository;

    @Transactional(value = "mssqlTransactionManager")
    public LotCarrierMapping save(LotCarrierMapping mapping) {
        return lotCarrierMappingRepository.save(mapping);
    }

    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public List<LotCarrierMapping> findAll() {
        return lotCarrierMappingRepository.findAll();
    }

    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public Optional<LotCarrierMapping> findById(Long id) {
        return lotCarrierMappingRepository.findById(id);
    }

    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public List<LotCarrierMapping> findByLotName(String lotName) {
        return lotCarrierMappingRepository.findByLotName(lotName);
    }

    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public Optional<LotCarrierMapping> findByCarrierName(String carrierName) {
        return lotCarrierMappingRepository.findByCarrierName(carrierName);
    }

    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public List<LotCarrierMapping> findByOrderIdAndOrderLineNumber(String orderId, String orderLineNumber) {
        return lotCarrierMappingRepository.findByOrderIdAndOrderLineNumber(orderId, orderLineNumber);
    }

    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public Optional<LotCarrierMapping> findByMngKey(Long mngKey) {
        return lotCarrierMappingRepository.findByMngKey(mngKey);
    }

    @Transactional(value = "mssqlTransactionManager")
    public void deleteAllByIdInBatch(List<Long> ids) {
        lotCarrierMappingRepository.deleteAllByIdInBatch(ids);
    }

    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public List<LotCarrierMappingHistory> findHistoryByPeriod(LocalDateTime start, LocalDateTime end) {
        return lotCarrierMappingRepository.findHistoryByPeriod(start, end);
    }

    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public List<LotCarrierMapping> findMantiTimeoutTargets(String state, LocalDateTime thresholdTime) {
        return lotCarrierMappingRepository.findByMantiRequestStateAndMantiRequestTimeBeforeAndMantiReplyTimeIsNull(state, thresholdTime);
    }
}