package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.LotEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LotJpaRepository extends JpaRepository<LotEntity, Long> {
    Optional<LotEntity> findByLotName(String lotName);
}