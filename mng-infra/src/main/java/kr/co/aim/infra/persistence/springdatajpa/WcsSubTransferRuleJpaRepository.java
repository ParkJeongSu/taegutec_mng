package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.WcsSubTransferRuleEntity;
import kr.co.aim.infra.persistence.entity.WcsSubTransferRuleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WcsSubTransferRuleJpaRepository extends JpaRepository<WcsSubTransferRuleEntity, WcsSubTransferRuleId> {

    List<WcsSubTransferRuleEntity> findByFactoryName(String factoryName);

    List<WcsSubTransferRuleEntity> findByFactoryNameAndEquipmentName(String factoryName, String equipmentName);

    List<WcsSubTransferRuleEntity> findByFactoryNameAndRouteLinkId(String factoryName, Long routeLinkId);
}
