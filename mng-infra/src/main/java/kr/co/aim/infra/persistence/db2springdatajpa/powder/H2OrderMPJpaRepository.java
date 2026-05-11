package kr.co.aim.infra.persistence.db2springdatajpa.powder;

import kr.co.aim.infra.persistence.db2entity.powder.H2OrderMPEntity;
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
public interface H2OrderMPJpaRepository extends JpaRepository<H2OrderMPEntity, Long> {
    Optional<H2OrderMPEntity> findByLineId(Long lineId);
    List<H2OrderMPEntity> findByIdocId(Long idocId);
    Page<H2OrderMPEntity> findByIdocId(Long idocId, Pageable pageable);
}
