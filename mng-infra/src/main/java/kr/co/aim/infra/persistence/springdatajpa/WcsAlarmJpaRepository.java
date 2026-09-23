package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.WcsAlarmEntity;
import kr.co.aim.infra.persistence.entity.WcsAlarmId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WcsAlarmJpaRepository extends JpaRepository<WcsAlarmEntity, WcsAlarmId> {

    List<WcsAlarmEntity> findByFactoryNameAndEquipmentName(String factoryName, String equipmentName);
}
