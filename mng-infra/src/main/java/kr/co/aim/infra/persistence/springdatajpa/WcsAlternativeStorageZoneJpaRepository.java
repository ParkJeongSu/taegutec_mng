package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.WcsAlternativeStorageZoneEntity;
import kr.co.aim.infra.persistence.entity.WcsAlternativeStorageZoneId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WcsAlternativeStorageZoneJpaRepository extends JpaRepository<WcsAlternativeStorageZoneEntity, WcsAlternativeStorageZoneId> {

    List<WcsAlternativeStorageZoneEntity> findByFactoryName(String factoryName);

    List<WcsAlternativeStorageZoneEntity> findByFactoryNameAndSourceZoneName(String factoryName, String sourceZoneName);

    List<WcsAlternativeStorageZoneEntity> findByFactoryNameAndSourceZoneNameOrderByPriorityAsc(String factoryName, String sourceZoneName);
}
