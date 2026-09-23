package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.WcsTransferCommandHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WcsTransferCommandHistoryJpaRepository extends JpaRepository<WcsTransferCommandHistoryEntity, String> {
}
