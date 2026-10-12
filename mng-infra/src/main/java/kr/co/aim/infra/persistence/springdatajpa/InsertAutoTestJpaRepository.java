package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.InsertAutoTestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InsertAutoTestJpaRepository extends JpaRepository<InsertAutoTestEntity, Long> {

    List<InsertAutoTestEntity> findByWorkStationId(String workStationId);

    List<InsertAutoTestEntity> findByCarrierName(String carrierName);
}
