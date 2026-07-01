package kr.co.aim.infra.persistence.springdatajpa;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import kr.co.aim.infra.persistence.entity.PortEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PortJpaRepository extends JpaRepository<PortEntity, Long> {
    Optional<PortEntity> findByEquipmentNameAndPortName(String equipmentName, String portName);
    // 2. 비관적 락 조회 (FOR UPDATE)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({
            @QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000") // 3초 대기
    })
    @Query("SELECT p FROM PortEntity p WHERE p.equipmentName = :equipmentName AND p.portName = :portName")
    Optional<PortEntity> findWithLockByEquipmentNameAndPortName(
            @Param("equipmentName") String equipmentName,
            @Param("portName") String portName
    );

    List<PortEntity> findByTransportState(String transportState);

    @Query("SELECT p FROM PortEntity p " +
            "JOIN PortDefEntity pd ON p.equipmentName = pd.equipmentName AND p.portName = pd.portName " +
            "WHERE p.transportState = :transportState " +
            "AND pd.portRoleType = :portRoleType")
    List<PortEntity> findByTransportStateAndPortRoleType(
            @Param("transportState") String transportState,
            @Param("portRoleType") String portRoleType
    );

    @Query("SELECT p FROM PortEntity p " +
            "JOIN PortDefEntity pd ON p.equipmentName = pd.equipmentName AND p.portName = pd.portName " +
            "WHERE p.transportState = :transportState " +
            "AND pd.detailPortType IN :detailPortType")
    List<PortEntity> findByTransportStateAndDetailPortTypeIn(
            @Param("transportState") String transportState,
            @Param("detailPortType") List<String> detailPortType
    );
}
