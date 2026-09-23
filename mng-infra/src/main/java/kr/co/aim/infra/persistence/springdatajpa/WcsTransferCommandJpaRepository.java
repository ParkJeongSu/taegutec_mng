package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.WcsTransferCommandEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WcsTransferCommandJpaRepository extends JpaRepository<WcsTransferCommandEntity, String> {

    List<WcsTransferCommandEntity> findByFactoryName(String factoryName);

    List<WcsTransferCommandEntity> findByCarrierName(String carrierName);
}
