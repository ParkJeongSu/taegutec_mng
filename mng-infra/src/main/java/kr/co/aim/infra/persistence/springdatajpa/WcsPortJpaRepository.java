package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.WcsPortEntity;
import kr.co.aim.infra.persistence.entity.WcsPortId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WcsPortJpaRepository extends JpaRepository<WcsPortEntity, WcsPortId> {

    List<WcsPortEntity> findByFactoryNameAndEquipmentName(String factoryName, String equipmentName);
}
