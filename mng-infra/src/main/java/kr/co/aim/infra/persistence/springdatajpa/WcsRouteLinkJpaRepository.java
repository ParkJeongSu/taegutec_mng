package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.WcsRouteLinkEntity;
import kr.co.aim.infra.persistence.entity.WcsRouteLinkId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WcsRouteLinkJpaRepository extends JpaRepository<WcsRouteLinkEntity, WcsRouteLinkId> {

    List<WcsRouteLinkEntity> findByFactoryName(String factoryName);

    List<WcsRouteLinkEntity> findByFactoryNameAndFromNodeId(String factoryName, String fromNodeId);

    List<WcsRouteLinkEntity> findByFactoryNameAndToNodeId(String factoryName, String toNodeId);
}
