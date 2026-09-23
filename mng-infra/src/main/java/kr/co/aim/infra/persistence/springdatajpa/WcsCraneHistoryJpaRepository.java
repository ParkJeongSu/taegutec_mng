package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.WcsCraneHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WcsCraneHistoryJpaRepository extends JpaRepository<WcsCraneHistoryEntity, String> {
}
