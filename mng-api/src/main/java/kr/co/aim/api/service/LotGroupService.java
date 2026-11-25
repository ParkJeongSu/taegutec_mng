package kr.co.aim.api.service;

import kr.co.aim.domain.repository.condition.LotGroupSearchCondition;
import kr.co.aim.domain.model.LotGroup;
import kr.co.aim.domain.repository.LotGroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LotGroupService {

    private final LotGroupRepository lotGroupRepository;

    @Transactional(value = "mssqlTransactionManager")
    public LotGroup save(LotGroup lot) {
        return lotGroupRepository.save(lot);
    }

    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public LotGroup findById(Long id) {
        Optional<LotGroup> optional = lotGroupRepository.findById(id);
        if (optional.isEmpty()) {
            throw new IllegalArgumentException("해당 LotGroup 기준정보가 존재하지 않습니다. ID: " + id);
        }
        return optional.get();
    }

    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public Page<LotGroup> findLotGroupWithConditions(LotGroupSearchCondition condition, Pageable pageable) {
        return lotGroupRepository.findLotGroupWithConditions(condition, pageable);
    }

    @Transactional(value = "mssqlTransactionManager")
    public void deleteAllByIdInBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return;
        lotGroupRepository.deleteAllByIdInBatch(ids);
    }

}