package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.AlarmActionEntity;
import kr.co.aim.infra.persistence.entity.AlarmActionUserGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlarmActionUserGroupJpaRepository extends JpaRepository<AlarmActionUserGroupEntity, Long> {
    Optional<AlarmActionUserGroupEntity> findByUserGroupName(String userGroupName);
}
