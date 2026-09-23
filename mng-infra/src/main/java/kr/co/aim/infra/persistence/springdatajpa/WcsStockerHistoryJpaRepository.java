package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.WcsStockerHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WcsStockerHistoryJpaRepository extends JpaRepository<WcsStockerHistoryEntity, String> {

    List<WcsStockerHistoryEntity> findByFactoryNameAndStockerNameOrderByEventTimeDesc(String factoryName, String stockerName);
}
