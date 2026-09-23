package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.WcsAlarmHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WcsAlarmHistoryJpaRepository extends JpaRepository<WcsAlarmHistoryEntity, String> {
}
