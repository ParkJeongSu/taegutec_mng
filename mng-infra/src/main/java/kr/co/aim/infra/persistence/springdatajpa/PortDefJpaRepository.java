package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.domain.model.PortDef;
import kr.co.aim.infra.persistence.entity.PortDefEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PortDefJpaRepository extends JpaRepository<PortDefEntity, Long> {

    Optional<PortDefEntity> findByEquipmentNameAndPortName(String equipmentName, String portName);

}
