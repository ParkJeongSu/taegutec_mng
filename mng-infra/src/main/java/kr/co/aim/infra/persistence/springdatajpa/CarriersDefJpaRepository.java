package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.CarrierDefEntity;
import kr.co.aim.infra.persistence.entity.CarriersEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CarriersDefJpaRepository extends JpaRepository<CarrierDefEntity, Long> {

    Optional<CarrierDefEntity> findByCarrierDefName(String carrierDefName);
}
