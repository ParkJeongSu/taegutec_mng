package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.WcsConveyorHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WcsConveyorHistoryJpaRepository extends JpaRepository<WcsConveyorHistoryEntity, String> {
}
