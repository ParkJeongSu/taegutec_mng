package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.AlarmDefEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlarmDefJpaRepository extends JpaRepository<AlarmDefEntity, Long> {
    List<AlarmDefEntity> findByAlarmDefName(String alarmCodeName);
}
