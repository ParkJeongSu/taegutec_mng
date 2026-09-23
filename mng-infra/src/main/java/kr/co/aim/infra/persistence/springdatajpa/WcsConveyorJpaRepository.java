package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.WcsConveyorEntity;
import kr.co.aim.infra.persistence.entity.WcsConveyorId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WcsConveyorJpaRepository extends JpaRepository<WcsConveyorEntity, WcsConveyorId> {

    List<WcsConveyorEntity> findByFactoryNameAndConveyorGroup(String factoryName, String conveyorGroup);
}
