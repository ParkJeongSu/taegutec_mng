package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.AlarmEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlarmJpaRepository extends JpaRepository<AlarmEntity, Long> {
    List<AlarmEntity> findByAlarmDefIdAndEquipmentName(Long alarmDefId,String equipmentName);
}
