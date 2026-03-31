package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.InterfaceEventLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterfaceEventLogJpaRepository extends JpaRepository<InterfaceEventLogEntity, Long> {

    List<InterfaceEventLogEntity> findByIfStatusOrderByCreateTimeAsc(String ifStatus);
}
