package kr.co.aim.domain.repository;

import kr.co.aim.domain.repository.condition.LotSearchCondition;
import kr.co.aim.domain.model.Lot;
import kr.co.aim.domain.model.LotHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LotRepository {
    List<Lot> findAll();
    Optional<Lot> findById(Long id);
    List<Lot> findByLotName(String lotName);
    List<Lot> findByLotNameAndProductionStatusNot(String lotName, String productionStatus);
    Optional<Lot> findByLotNameAndCarrierName(String lotName,String carrierName);
    Optional<Lot> findByCarrierName(String carrierName);
    List<Lot> findByOrderIdAndOrderLineNumber(String orderId, String orderLineNumber);
    List<Lot> findByMngKey(Long mngKey);
    Lot save(Lot mapping);
    void deleteAllByIdInBatch(List<Long> ids);
    List<LotHistory> findHistoryByPeriod(LocalDateTime start, LocalDateTime end);
    List<Lot> findByMantiRequestStateAndMantiRequestTimeBeforeAndMantiReplyTimeIsNull(
            String mantiRequestState,
            LocalDateTime thresholdTime
    );
    Page<Lot> findLotWithConditions(LotSearchCondition condition, Pageable pageable);
    List<Lot> findByOrderIdAndOrderLineNumberAndProductionStatusIn(String orderId, String orderLineNumber,List<String> productionStatus);
    List<Lot> findLotForUnpacking(String lotName, String carrierType);
}