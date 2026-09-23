package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.WcsShelfEntity;
import kr.co.aim.infra.persistence.entity.WcsShelfId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WcsShelfJpaRepository extends JpaRepository<WcsShelfEntity, WcsShelfId> {

    List<WcsShelfEntity> findByFactoryNameAndStockerName(String factoryName, String stockerName);

    Optional<WcsShelfEntity> findByFactoryNameAndStockerNameAndShelfName(String factoryName, String stockerName, String shelfName);

    boolean existsByFactoryNameAndStockerNameAndShelfName(String factoryName, String stockerName, String shelfName);

    void deleteByFactoryNameAndStockerNameAndShelfName(String factoryName, String stockerName, String shelfName);
}
