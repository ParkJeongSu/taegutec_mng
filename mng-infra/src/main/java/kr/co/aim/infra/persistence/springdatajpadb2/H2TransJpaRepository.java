package kr.co.aim.infra.persistence.springdatajpadb2;

import kr.co.aim.infra.persistence.entitydb2.H2TransEntity;
import kr.co.aim.infra.persistence.entitydb2.IdocEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface H2TransJpaRepository extends JpaRepository<H2TransEntity, Long> {
    Optional<H2TransEntity> findByLineId(Long lineId);
    List<H2TransEntity> findByIdocId(Long idocId);
    Page<H2TransEntity> findByIdocId(Long idocId,Pageable pageable);
}
