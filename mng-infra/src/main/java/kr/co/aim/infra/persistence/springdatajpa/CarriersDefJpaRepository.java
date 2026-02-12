package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.CarrierDefEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarriersDefJpaRepository extends JpaRepository<CarrierDefEntity, Long> {

    Optional<CarrierDefEntity> findByCarrierDefName(String carrierDefName);
}
