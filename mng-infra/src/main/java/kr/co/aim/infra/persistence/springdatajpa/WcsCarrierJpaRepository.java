package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.WcsCarrierEntity;
import kr.co.aim.infra.persistence.entity.WcsCarrierId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WcsCarrierJpaRepository extends JpaRepository<WcsCarrierEntity, WcsCarrierId> {

    Optional<WcsCarrierEntity> findByCarrierNameAndFactoryName(String carrierName, String factoryName);

    List<WcsCarrierEntity> findByFactoryName(String factoryName);

    boolean existsByCarrierNameAndFactoryName(String carrierName, String factoryName);

    void deleteByCarrierNameAndFactoryName(String carrierName, String factoryName);
}
