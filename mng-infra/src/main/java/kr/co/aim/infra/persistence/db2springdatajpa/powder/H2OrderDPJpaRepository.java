package kr.co.aim.infra.persistence.db2springdatajpa.powder;

import kr.co.aim.infra.persistence.db2entity.powder.H2OrderDPEntity;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@ConditionalOnProperty(name = "factory.type", havingValue = "powder")
public interface H2OrderDPJpaRepository extends JpaRepository<H2OrderDPEntity, Long> {
    Optional<H2OrderDPEntity> findByLineId(Long lineId);
    List<H2OrderDPEntity> findByIdocId(Long idocId);
    Page<H2OrderDPEntity> findByIdocId(Long idocId, Pageable pageable);
    @Query("SELECT h FROM H2OrderDPEntity h WHERE h.cOrderId = :cOrderId")
    List<H2OrderDPEntity> findByCOrderId(String cOrderId);
}
