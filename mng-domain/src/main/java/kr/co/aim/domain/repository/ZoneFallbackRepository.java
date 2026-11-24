package kr.co.aim.domain.repository;

import kr.co.aim.domain.model.ZoneFallback;
import kr.co.aim.domain.repository.condition.ZoneFallbackSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ZoneFallbackRepository {

    List<ZoneFallback> findAll();

    Optional<ZoneFallback> findById(Long id);

    ZoneFallback save(ZoneFallback zoneFallback);

    void deleteById(Long id);

    void deleteAllByIdInBatch(List<Long> ids);

    Page<ZoneFallback> findZoneFallbacksWithConditions(ZoneFallbackSearchCondition condition, Pageable pageable);
}
