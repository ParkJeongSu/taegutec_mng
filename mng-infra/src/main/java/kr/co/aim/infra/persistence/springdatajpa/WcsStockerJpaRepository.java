package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.WcsStockerEntity;
import kr.co.aim.infra.persistence.entity.WcsStockerId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WcsStockerJpaRepository extends JpaRepository<WcsStockerEntity, WcsStockerId> {

    Optional<WcsStockerEntity> findByFactoryNameAndStockerName(String factoryName, String stockerName);

    List<WcsStockerEntity> findByFactoryName(String factoryName);

    boolean existsByFactoryNameAndStockerName(String factoryName, String stockerName);

    void deleteByFactoryNameAndStockerName(String factoryName, String stockerName);
}
