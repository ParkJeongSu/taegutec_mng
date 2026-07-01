package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.CarrierEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CarrierJpaRepository extends JpaRepository<CarrierEntity, Long> {
    Optional<CarrierEntity> findByCarrierName(String carrierName);

    @Query("SELECT c FROM CarrierEntity c " +
            "WHERE c.cleanState = :cleanState " +
            "AND c.transportState = :transportState " +
            "AND COALESCE(c.transportJobId, '') = COALESCE(:jobId, '') " +
            "AND c.useState = :useState " +
            "AND c.quantity = :qty " +
            "AND c.containerType IN :types " +
            "ORDER BY c.inboundTime ASC"
    )
    List<CarrierEntity> findCarriersForEmptyContainer(
            @Param("cleanState") String cleanState,
            @Param("transportState") String transportState,
            @Param("jobId") String transportJobId,
            @Param("useState") String useState,
            @Param("qty") Integer quantity,
            @Param("types") List<String> containerTypes
    );

    @Query("SELECT c FROM CarrierEntity c " +
            "WHERE c.cleanState = :cleanState " +
            "AND c.transportState = :transportState " +
            "AND COALESCE(c.transportJobId, '') = COALESCE(:jobId, '') " +
            "AND c.useState = :useState " +
            "ORDER BY c.inboundTime ASC"
    )
    List<CarrierEntity> findCarriersForFullContainer(
            @Param("cleanState") String cleanState,
            @Param("transportState") String transportState,
            @Param("jobId") String transportJobId,
            @Param("useState") String useState
    );

}
