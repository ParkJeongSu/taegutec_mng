package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.AlarmHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmHistoryJpaRepository extends JpaRepository<AlarmHistoryEntity, Long> {
}
