package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.WcsCarrierHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WcsCarrierHistoryJpaRepository extends JpaRepository<WcsCarrierHistoryEntity, String> {

    List<WcsCarrierHistoryEntity> findByCarrierNameAndFactoryNameOrderByEventTimeDesc(String carrierName, String factoryName);

    List<WcsCarrierHistoryEntity> findByFactoryNameOrderByEventTimeDesc(String factoryName);
}
