package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.WcsShelfHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WcsShelfHistoryJpaRepository extends JpaRepository<WcsShelfHistoryEntity, String> {

    List<WcsShelfHistoryEntity> findByFactoryNameAndStockerNameAndShelfNameOrderByEventTimeDesc(String factoryName, String stockerName, String shelfName);

    List<WcsShelfHistoryEntity> findByFactoryNameAndStockerNameOrderByEventTimeDesc(String factoryName, String stockerName);
}
