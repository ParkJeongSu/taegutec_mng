package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.LotGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LotGroupJpaRepository extends JpaRepository<LotGroupEntity, Long> {
    Optional<LotGroupEntity> findByLotGroupName(String lotGroupName);
}