package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.ProductDefHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductDefHistoryJpaRepository extends JpaRepository<ProductDefHistoryEntity, Long> {

}
