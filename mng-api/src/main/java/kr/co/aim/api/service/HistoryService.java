package kr.co.aim.api.service;

import kr.co.aim.common.handler.IBaseHistoryEntity;
import kr.co.aim.infra.persistence.repository.GenericHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 만들어줍니다. (DI)
@Slf4j
public class HistoryService {
    private final GenericHistoryRepository historyRepository;

    // (저장 로직은 어제와 동일)
    @Transactional(value = "mssqlTransactionManager")
    public <T extends IBaseHistoryEntity> T saveHistory(T historyEntity) {
        return historyRepository.save(historyEntity);
    }
}
