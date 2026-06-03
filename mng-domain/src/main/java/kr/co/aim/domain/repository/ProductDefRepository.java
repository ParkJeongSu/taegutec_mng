package kr.co.aim.domain.repository;

import kr.co.aim.common.condition.EquipmentSearchCondition;
import kr.co.aim.common.condition.ProductDefSearchCondition;
import kr.co.aim.domain.model.Equipment;
import kr.co.aim.domain.model.ProductDef;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 사용자 저장소의 기능을 정의하는 인터페이스.
 * 애플리케이션의 다른 부분(서비스 계층 등)은 이 인터페이스에만 의존합니다.
 * 실제 구현 기술(JPA, JDBC 등)과는 완전히 분리됩니다.
 */
public interface ProductDefRepository {
    /**
     * 모든 사용자를 찾습니다.
     * @return 모든 CarrierDef 도메인 객체 리스트
     */
    List<ProductDef> findAll();

    /**
     * ID로 사용자를 찾습니다.
     * @param id carrierDef ID
     * @return Optional<CarrierDef>
     */
    Optional<ProductDef> findById(Long id);

    /**
     * productDefName로 productDef를 찾습니다.
     * @param productDefName productDefName
     * @return Optional<productDefName>
     */
    Optional<ProductDef> findByProductDefName(String productDefName);


    ProductDef save(ProductDef productDef);

    List<ProductDef> save(List<ProductDef> productDefList);

    void deleteAllByIdInBatch(List<Long>ids);

    Page<ProductDef> findProductDefByCondition(ProductDefSearchCondition condition, Pageable pageable);
}
