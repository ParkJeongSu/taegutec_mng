package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.SiteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SiteJpaRepository extends JpaRepository<SiteEntity, Long> {
    Optional<SiteEntity> findBySiteName(String siteName);
}
