package kr.co.aim.infra.persistence.springdatajpadb2;

import kr.co.aim.infra.persistence.entitydb2.H2OrderMEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface H2OrderMJpaRepository extends JpaRepository<H2OrderMEntity, Long> {
    Optional<H2OrderMEntity> findByLineId(Long lineId);
    List<H2OrderMEntity> findByIdocId(Long idocId);
    Page<H2OrderMEntity> findByIdocId(Long idocId,Pageable pageable);
    @Query("SELECT h FROM H2OrderMEntity h WHERE h.cOrderId = :cOrderId")
    List<H2OrderMEntity> findByCOrderId(String cOrderId);
}
