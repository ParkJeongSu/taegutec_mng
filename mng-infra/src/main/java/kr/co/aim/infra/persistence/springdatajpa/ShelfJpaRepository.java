package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.ShelfEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShelfJpaRepository extends JpaRepository<ShelfEntity, Long> {
    Optional<ShelfEntity> findByLocationId(String locationId);
}
