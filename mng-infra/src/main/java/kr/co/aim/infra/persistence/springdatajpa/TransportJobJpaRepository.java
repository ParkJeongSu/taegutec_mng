package kr.co.aim.infra.persistence.springdatajpa;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import kr.co.aim.infra.persistence.entity.ProductionOrderEntity;
import kr.co.aim.infra.persistence.entity.TransportJobEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransportJobJpaRepository extends JpaRepository<TransportJobEntity, Long> {

    Optional<TransportJobEntity> findByTransportJobName(String transportJobName);

    Optional<TransportJobEntity> findByOrderId(String orderId);

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

    // 2. 비관적 락 조회 (FOR UPDATE)
    // TODO: 비관적 LOCK으로 개발 2026.04.03하기
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({
            @QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000") // 3초 대기
    })
    @Query("SELECT t FROM TransportJobEntity t WHERE t.transportJobName = :transportJobName ")
    Optional<TransportJobEntity> findWithLockByTransportJobName(
            @Param("transportJobName") String transportJobName
    );

    // 금일 특정 기간(00:00:00 ~ 23:59:59) 동안 생성된 order 조회
    List<TransportJobEntity> findByCreateTimeBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);

    // 금일 생성된 완료 order 조회용 (기간 + 상태 조건)
    List<TransportJobEntity> findByCreateTimeBetweenAndTransportJobState(
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            String transportJobState
    );
}
