package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.AlarmMailActionDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlarmMailActionDetailJpaRepository extends JpaRepository<AlarmMailActionDetailEntity, Long> {
    List<AlarmMailActionDetailEntity> findByAlarmActionId(Long alarmActionId);
    List<AlarmMailActionDetailEntity> findByAlarmActionUserGroupId(Long alarmActionUserGroupId);
    Optional<AlarmMailActionDetailEntity> findByAlarmActionIdAndAlarmActionUserGroupId(Long alarmActionId,Long alarmActionUserGroupId);
}
