package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.InsertAutoRelocationTestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InsertAutoRelocationTestJpaRepository extends JpaRepository<InsertAutoRelocationTestEntity, Long> {

    List<InsertAutoRelocationTestEntity> findByCarrierName(String carrierName);
}
