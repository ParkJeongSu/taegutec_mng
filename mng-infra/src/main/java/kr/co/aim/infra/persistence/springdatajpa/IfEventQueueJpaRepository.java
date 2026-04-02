package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.IfEventQueueEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IfEventQueueJpaRepository extends JpaRepository<IfEventQueueEntity, Long> {

    List<IfEventQueueEntity> findByIfStatusOrderByCreateTimeAsc(String ifStatus);
}
