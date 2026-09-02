package kr.co.aim.api.service;

import kr.co.aim.common.condition.LotCarrierMappingSearchCondition;
import kr.co.aim.domain.model.LotCarrierMapping;
import kr.co.aim.domain.model.LotCarrierMappingHistory;
import kr.co.aim.domain.repository.LotCarrierMappingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
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

    @Transactional(value = "mssqlTransactionManager")
    public LotCarrierMapping findById(Long id) {
        Optional<LotCarrierMapping> optional = lotCarrierMappingRepository.findById(id);
        if (optional.isEmpty()) {
            throw new IllegalArgumentException("해당 Lot Carrier Mapping 정보가 존재하지 않습니다. ID: " + id);
        }
        return optional.get();
    }

    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public Page<LotCarrierMapping> findLotCarrierMappingWithConditions(LotCarrierMappingSearchCondition condition, Pageable pageable) {
        return lotCarrierMappingRepository.findLotCarrierMappingWithConditions(condition, pageable);
    }

    @Transactional(value = "mssqlTransactionManager")
    public void deleteAllByIdInBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return;
        lotCarrierMappingRepository.deleteAllByIdInBatch(ids);
    }

    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public List<LotCarrierMapping> findMantiTimeoutTargets(String state, LocalDateTime thresholdTime) {
        return lotCarrierMappingRepository.findByMantiRequestStateAndMantiRequestTimeBeforeAndMantiReplyTimeIsNull(state, thresholdTime);
    }
}