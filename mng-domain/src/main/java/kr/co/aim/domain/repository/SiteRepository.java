package kr.co.aim.domain.repository;

import kr.co.aim.domain.model.Site;
import kr.co.aim.domain.repository.condition.SiteSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface SiteRepository {

    List<Site> findAll();

    Optional<Site> findById(Long id);

    Optional<Site> findBySiteName(String siteName);

    Site save(Site site);

    void deleteById(Long id);

    void deleteAllByIdInBatch(List<Long> ids);

    Page<Site> findSitesWithConditions(SiteSearchCondition condition, Pageable pageable);
}
