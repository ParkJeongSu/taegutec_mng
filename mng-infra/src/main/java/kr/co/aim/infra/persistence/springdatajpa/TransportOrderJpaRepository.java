package kr.co.aim.infra.persistence.springdatajpa;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import kr.co.aim.infra.persistence.entity.TransportOrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TransportOrderJpaRepository extends JpaRepository<TransportOrderEntity, Long> {
    Page<TransportOrderEntity> findAll(Pageable pageable);

    Optional<TransportOrderEntity> findByTransportOrderId(String transportOrderId);

    @Query("SELECT t FROM TransportOrderEntity t " +
            "WHERE t.carrierName = :carrierName " +
            "AND t.transportType = :transportType " +
            "AND t.transportStatus IN :transportStatus " +
            "ORDER BY t.eventTime DESC"
    )
    List<TransportOrderEntity> findTransportOrderByCondition(
            @Param("carrierName") String carrierName,
            @Param("transportType") String transportType,
            @Param("transportStatus") List<String> transportStatus
    );

    // 2. 비관적 락 조회 (FOR UPDATE)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({
            @QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000") // 3초 대기
    })
    @Query("SELECT t FROM TransportOrderEntity t WHERE t.id = :id ")
    Optional<TransportOrderEntity> findWithLockById(
            @Param("id") Long id
    );
}
