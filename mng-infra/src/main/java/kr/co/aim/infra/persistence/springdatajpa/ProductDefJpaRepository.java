package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.ProductDefEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductDefJpaRepository extends JpaRepository<ProductDefEntity, Long> {
    Optional<ProductDefEntity> findByProductDefName(String productDefName);
}
