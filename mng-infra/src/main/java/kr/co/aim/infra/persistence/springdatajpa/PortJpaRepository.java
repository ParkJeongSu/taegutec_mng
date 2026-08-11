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

    List<PortEntity> findByEquipmentNameAndPortType(String equipmentName,String portType);

    List<PortEntity> findByTransportState(String transportState);

    @Query("SELECT p FROM PortEntity p " +
            "JOIN PortDefEntity pd ON p.equipmentName = pd.equipmentName AND p.portName = pd.portName " +
            "JOIN TransportOrderEntity to ON pd.workCenterName = to.workStationId " +
            "WHERE p.transportState = :transportState " +
            "AND to.transportType = :transportType " +
            "AND to.transportStatus = :transportStatus " +
            "AND to.createTime = (" +
            "    SELECT MIN(subTo.createTime) " +
            "    FROM TransportOrderEntity subTo " +
            "    WHERE subTo.workStationId = pd.workCenterName " +
            "      AND subTo.transportType = :transportType " +
            "      AND subTo.transportStatus = :transportStatus" +
            ") " +
            "AND p.eventTime = (" +
            "    SELECT MIN(subP.eventTime) " +
            "    FROM PortEntity subP " +
            "    JOIN PortDefEntity subPd ON subP.equipmentName = subPd.equipmentName AND subP.portName = subPd.portName " +
            "    WHERE subPd.workCenterName = pd.workCenterName " +
            "      AND subP.transportState = :transportState" +
            ")")
    List<PortEntity> findPortsWithEarliestTransportOrder(
            @Param("transportState") String transportState,
            @Param("transportType") String transportType,
            @Param("transportStatus") String transportStatus
    );


    @Query(value = "SELECT * FROM (" +
            "  SELECT p.*, " +
            "         ROW_NUMBER() OVER (PARTITION BY pd.WORK_CENTER_NAME ORDER BY p.EVENT_TIME ASC) as rn " +
            "  FROM NEXBEMNG.dbo.PORT p " +
            "  JOIN NEXBEDEF.dbo.PORT_DEF pd ON p.EQUIPMENT_NAME = pd.EQUIPMENT_NAME AND p.PORT_NAME = pd.PORT_NAME " +
            "  WHERE p.TRANSPORT_STATE = :transportState " +
            "  AND pd.DETAIL_PORT_TYPE IN (:detailPortType) " +
            ") sub " +
            "WHERE sub.rn = 1", nativeQuery = true)
    List<PortEntity> findEarliestPortPerWorkCenter(@Param("transportState") String transportState, @Param("detailPortType") List<String> detailPortType);


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
