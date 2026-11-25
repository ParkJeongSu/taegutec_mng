package kr.co.aim.domain.repository;

import kr.co.aim.domain.repository.condition.LotGroupSearchCondition;
import kr.co.aim.domain.model.LotGroup;
import kr.co.aim.domain.model.LotGroupHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LotGroupRepository {
    List<LotGroup> findAll();
    Optional<LotGroup> findById(Long id);
    Optional<LotGroup> findByLotGroupName(String lotGroupName);
    LotGroup save(LotGroup lot);
    void deleteAllByIdInBatch(List<Long> ids);
    List<LotGroupHistory> findLotGroupHistoryByPeriod(LocalDateTime start, LocalDateTime end);
    Page<LotGroup> findLotGroupWithConditions(LotGroupSearchCondition condition, Pageable pageable);
}