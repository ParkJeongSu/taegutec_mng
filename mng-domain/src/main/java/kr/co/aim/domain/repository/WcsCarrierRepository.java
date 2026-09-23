package kr.co.aim.domain.repository;

import kr.co.aim.common.condition.WcsCarrierSearchCondition;
import kr.co.aim.domain.model.WcsCarrier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface WcsCarrierRepository {

    List<WcsCarrier> findAll();

    Optional<WcsCarrier> findById(String carrierName, String factoryName);

    List<WcsCarrier> findByFactoryName(String factoryName);

    boolean existsById(String carrierName, String factoryName);

    WcsCarrier save(WcsCarrier carrier);

    void deleteById(String carrierName, String factoryName);

    Page<WcsCarrier> findCarriers(WcsCarrierSearchCondition condition, Pageable pageable);
}
