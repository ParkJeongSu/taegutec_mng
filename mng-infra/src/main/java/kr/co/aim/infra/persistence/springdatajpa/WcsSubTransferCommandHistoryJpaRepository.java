package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.WcsSubTransferCommandHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WcsSubTransferCommandHistoryJpaRepository extends JpaRepository<WcsSubTransferCommandHistoryEntity, String> {
}
