package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.AlarmActionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlarmActionJpaRepository extends JpaRepository<AlarmActionEntity, Long> {
    List<AlarmActionEntity> findByAlarmDefId(Long alarmIDefId);
    Optional<AlarmActionEntity> findByAlarmActionName(String alarmActionName);
}
