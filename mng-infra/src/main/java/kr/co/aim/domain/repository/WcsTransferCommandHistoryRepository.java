package kr.co.aim.domain.repository;

import kr.co.aim.common.condition.WcsTransferCommandHistorySearchCondition;
import kr.co.aim.infra.persistence.entity.WcsTransferCommandHistoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WcsTransferCommandHistoryRepository {

    Page<WcsTransferCommandHistoryEntity> findHistory(WcsTransferCommandHistorySearchCondition condition, Pageable pageable);
}
