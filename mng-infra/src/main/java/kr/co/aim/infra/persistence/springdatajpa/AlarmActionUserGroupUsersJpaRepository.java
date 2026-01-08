package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.AlarmActionUserGroupEntity;
import kr.co.aim.infra.persistence.entity.AlarmActionUserGroupUsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlarmActionUserGroupUsersJpaRepository extends JpaRepository<AlarmActionUserGroupUsersEntity, Long> {
    List<AlarmActionUserGroupUsersEntity> findByAlarmActionUserGroupId(Long alarmActionUserGroupId);
}
