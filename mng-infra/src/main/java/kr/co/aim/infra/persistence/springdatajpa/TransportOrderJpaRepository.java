package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.TransportOrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransportOrderJpaRepository extends JpaRepository<TransportOrderEntity, Long> {
    Page<TransportOrderEntity> findAll(Pageable pageable);

    TransportOrderEntity findByTransportOrderId(String transportOrderId);
}
