package kr.co.aim.domain.repository;

import kr.co.aim.domain.model.Lot;
import kr.co.aim.domain.model.LotHistory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LotRepository {
    List<Lot> findAll();
    Optional<Lot> findById(Long id);
    Optional<Lot> findByLotName(String lotName);
    Lot save(Lot lot);
    void deleteAllByIdInBatch(List<Long> ids);
    List<LotHistory> findLotHistoryByPeriod(LocalDateTime start, LocalDateTime end);
}