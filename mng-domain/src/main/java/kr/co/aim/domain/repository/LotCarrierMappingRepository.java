package kr.co.aim.domain.repository;

import kr.co.aim.common.condition.LotCarrierMappingSearchCondition;
import kr.co.aim.domain.model.LotCarrierMapping;
import kr.co.aim.domain.model.LotCarrierMappingHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LotCarrierMappingRepository {
    List<LotCarrierMapping> findAll();
    Optional<LotCarrierMapping> findById(Long id);
    List<LotCarrierMapping> findByLotName(String lotName);
    List<LotCarrierMapping> findByLotNameAndProductionStatusNot(String lotName, String productionStatus);
    Optional<LotCarrierMapping> findByLotNameAndCarrierName(String lotName,String carrierName);
    Optional<LotCarrierMapping> findByCarrierName(String carrierName);
    List<LotCarrierMapping> findByOrderIdAndOrderLineNumber(String orderId, String orderLineNumber);
    Optional<LotCarrierMapping> findByMngKey(Long mngKey);
    LotCarrierMapping save(LotCarrierMapping mapping);
    void deleteAllByIdInBatch(List<Long> ids);
    List<LotCarrierMappingHistory> findHistoryByPeriod(LocalDateTime start, LocalDateTime end);
    List<LotCarrierMapping> findByMantiRequestStateAndMantiRequestTimeBeforeAndMantiReplyTimeIsNull(
            String mantiRequestState,
            LocalDateTime thresholdTime
    );
    Page<LotCarrierMapping> findLotCarrierMappingWithConditions(LotCarrierMappingSearchCondition condition, Pageable pageable);
    List<LotCarrierMapping> findByOrderIdAndOrderLineNumberAndProductionStatusIn(String orderId, String orderLineNumber,List<String> productionStatus);
}