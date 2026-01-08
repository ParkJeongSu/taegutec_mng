package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.LotsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LotsJpaRepository extends JpaRepository<LotsEntity, Long> {
    Optional<LotsEntity> findByLotName(String lotName);
    List<LotsEntity> findByCarrierId(Long carrierId);
}
