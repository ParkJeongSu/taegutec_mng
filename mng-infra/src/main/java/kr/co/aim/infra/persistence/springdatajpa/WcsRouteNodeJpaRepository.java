package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.WcsRouteNodeEntity;
import kr.co.aim.infra.persistence.entity.WcsRouteNodeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WcsRouteNodeJpaRepository extends JpaRepository<WcsRouteNodeEntity, WcsRouteNodeId> {

    List<WcsRouteNodeEntity> findByFactoryName(String factoryName);

    List<WcsRouteNodeEntity> findByFactoryNameAndNodeId(String factoryName, String nodeId);
}
