package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.PortsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PortsJpaRepository extends JpaRepository<PortsEntity, Long> {
    Optional<PortsEntity> findByEquipmentNameAndPortName(String equipmentName,String portName);
}
