package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.AlarmActionEntity;
import kr.co.aim.infra.persistence.entity.IF_EVENT_LOGEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IF_EVENT_LOGJpaRepository extends JpaRepository<IF_EVENT_LOGEntity, Long> {
}
