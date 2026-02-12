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

    /**
     * 공통 히스토리 조회
     * @param entityClass     "alarm", "alarm_def" 등 URL에서 받은 리소스 이름
     * @param searchConditionDto 컨트롤러에서 받은 검색 조건 Map
     * @param pageable         페이지 정보
     * @return
     */
    @Transactional(readOnly = true)
    public Page<? extends IBaseHistoryEntity> getHistory(
            Class<? extends IBaseHistoryEntity> entityClass, // 클래스를 직접 받음
            Object searchConditionDto,
            Pageable pageable
    ) {
        // 받은 인자를 그대로 리포지토리로 전달
        return historyRepository.findHistory(entityClass, searchConditionDto, pageable);
    }

    // (저장 로직은 어제와 동일)
    @Transactional
    public <T extends IBaseHistoryEntity> T saveHistory(T historyLog) {
        return historyRepository.save(historyLog);
    }
}
