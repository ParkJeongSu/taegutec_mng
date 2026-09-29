package kr.co.aim.domain.repository;

import kr.co.aim.common.condition.WcsTransferCommandHistorySearchCondition;
import kr.co.aim.domain.model.WcsTransferCommandHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WcsTransferCommandHistoryRepository {

    Page<WcsTransferCommandHistory> findHistory(WcsTransferCommandHistorySearchCondition condition, Pageable pageable);
}
