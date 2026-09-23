package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.WcsZoneEntity;
import kr.co.aim.infra.persistence.entity.WcsZoneId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WcsZoneJpaRepository extends JpaRepository<WcsZoneEntity, WcsZoneId> {

    Optional<WcsZoneEntity> findByFactoryNameAndZoneName(String factoryName, String zoneName);

    List<WcsZoneEntity> findByFactoryName(String factoryName);

    boolean existsByFactoryNameAndZoneName(String factoryName, String zoneName);

    void deleteByFactoryNameAndZoneName(String factoryName, String zoneName);
}
