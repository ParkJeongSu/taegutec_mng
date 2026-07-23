package kr.co.aim.infra.persistence.springdatajpa;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import kr.co.aim.infra.persistence.entity.PortDefEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PortDefJpaRepository extends JpaRepository<PortDefEntity, Long> {

    @Query("SELECT p FROM PortDefEntity p WHERE p.equipmentName = :equipmentName AND p.portName = :portName")
    Optional<PortDefEntity> findByEquipmentNameAndPortName(
            @Param("equipmentName") String equipmentName,
            @Param("portName") String portName
    );

    Optional<PortDefEntity> findByLocationId(String locationId);

    // 2. 비관적 락 조회 (FOR UPDATE)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({
            @QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000") // 3초 대기
    })
    @Query("SELECT p FROM PortDefEntity p WHERE p.equipmentName = :equipmentName AND p.portName = :portName")
    Optional<PortDefEntity> findWithLockByEquipmentNameAndPortName(
            @Param("equipmentName") String equipmentName,
            @Param("portName") String portName
    );

    List<PortDefEntity> findByWorkCenterNameAndDetailPortTypeInAndPortTypeIn(
            String workCenterName,
            List<String> detailPortTypes,
            List<String> portTypes
    );

}
