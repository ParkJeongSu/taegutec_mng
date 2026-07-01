package kr.co.aim.api.service;

import kr.co.aim.domain.model.Lot;
import kr.co.aim.domain.model.LotHistory;
import kr.co.aim.domain.repository.LotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LotService {

    private final LotRepository lotRepository;

    @Transactional(value = "mssqlTransactionManager")
    public Lot save(Lot lot) {
        return lotRepository.save(lot);
    }

    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public List<Lot> findAll() {
        return lotRepository.findAll();
    }

    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public Optional<Lot> findById(Long id) {
        return lotRepository.findById(id);
    }

    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public Optional<Lot> findByLotName(String lotName) {
        return lotRepository.findByLotName(lotName);
    }

    @Transactional(value = "mssqlTransactionManager")
    public void deleteAllByIdInBatch(List<Long> ids) {
        lotRepository.deleteAllByIdInBatch(ids);
    }

    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public List<LotHistory> findLotHistoryByPeriod(LocalDateTime start, LocalDateTime end) {
        return lotRepository.findLotHistoryByPeriod(start, end);
    }
}