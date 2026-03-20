package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.domain.model.TransportJob;
import kr.co.aim.infra.persistence.entity.TransportJobEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransportJobJpaRepository extends JpaRepository<TransportJobEntity, Long> {
    Optional<TransportJobEntity> findByTransportJobName(String transportJobName);
    // destinationEquipmentName 과 transportJobState(여러 개)로 조회
    List<TransportJobEntity> findByDestinationEquipmentNameAndDestinationPortNameAndTransportJobStateIn(
            String destinationEquipmentName,
            String destinationPortName,
            List<String> transportJobStates
    );

    List<TransportJobEntity> findByCarrierNameAndTransportJobStateIn(
            String carrierName,
            List<String> transportJobStates
    );
}
