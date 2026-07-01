package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.LotCarrierMappingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LotCarrierMappingJpaRepository extends JpaRepository<LotCarrierMappingEntity, Long> {
    List<LotCarrierMappingEntity> findByLotName(String lotName);
    Optional<LotCarrierMappingEntity> findByCarrierName(String carrierName);
    List<LotCarrierMappingEntity> findByOrderIdAndOrderLineNumber(String orderId, String orderLineNumber);
    Optional<LotCarrierMappingEntity> findByMngKey(Long mngKey);
    List<LotCarrierMappingEntity> findByMantiRequestStateAndMantiRequestTimeBeforeAndMantiReplyTimeIsNull(
            String mantiRequestState,
            LocalDateTime thresholdTime
    );
}