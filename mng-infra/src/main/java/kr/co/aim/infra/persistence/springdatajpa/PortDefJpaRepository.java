package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.PortDefEntity;
import kr.co.aim.infra.persistence.entity.PortDefId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PortDefJpaRepository extends JpaRepository<PortDefEntity, PortDefId> {

    @Query("SELECT p FROM PortDefEntity p WHERE p.id.equipmentName = :equipmentName AND p.id.portName = :portName")
    Optional<PortDefEntity> findByEquipmentNameAndPortName(
            @Param("equipmentName") String equipmentName,
            @Param("portName") String portName
    );

}
