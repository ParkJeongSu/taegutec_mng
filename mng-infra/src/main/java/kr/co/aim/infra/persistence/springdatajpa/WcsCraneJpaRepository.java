package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.WcsCraneEntity;
import kr.co.aim.infra.persistence.entity.WcsCraneId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WcsCraneJpaRepository extends JpaRepository<WcsCraneEntity, WcsCraneId> {

    List<WcsCraneEntity> findByFactoryNameAndStockerName(String factoryName, String stockerName);
}
