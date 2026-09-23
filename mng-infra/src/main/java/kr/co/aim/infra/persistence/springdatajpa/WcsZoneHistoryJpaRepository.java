package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.WcsZoneHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WcsZoneHistoryJpaRepository extends JpaRepository<WcsZoneHistoryEntity, String> {

    List<WcsZoneHistoryEntity> findByFactoryNameAndZoneNameOrderByEventTimeDesc(String factoryName, String zoneName);

    List<WcsZoneHistoryEntity> findByFactoryNameOrderByEventTimeDesc(String factoryName);
}
