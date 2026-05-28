package kr.co.aim.infra.persistence.adapter;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.domain.model.ProductDef;
import kr.co.aim.domain.repository.ProductDefRepository;
import kr.co.aim.infra.persistence.entity.ProductDefEntity;
import kr.co.aim.infra.persistence.mapper.ProductDefHistoryMapper;
import kr.co.aim.infra.persistence.mapper.ProductDefMapper;
import kr.co.aim.infra.persistence.springdatajpa.ProductDefHistoryJpaRepository;
import kr.co.aim.infra.persistence.springdatajpa.ProductDefJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * UserRepository의 JPA 기반 구현체.
 * 실제 DB 작업은 Spring Data JPA가 제공하는 JpaRepository에 위임합니다.
 */

@Repository
@RequiredArgsConstructor
public class ProductDefRepositoryImpl implements ProductDefRepository {
    // Spring Data JPA가 자동으로 구현해주는 JPA 리포지토리. UserEntity를 다룬다.
    private final ProductDefJpaRepository productDefJpaRepository;
    private final ProductDefMapper productDefMapper;
    private final ProductDefHistoryJpaRepository productDefHistoryJpaRepository;
    private final ProductDefHistoryMapper productDefHistoryMapper;
    private final JPAQueryFactory queryFactory; // ✨ JPAQueryFactory 주입

    @Override
    public List<ProductDef> findAll() {
        return productDefJpaRepository.findAll().stream().map(productDefMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<ProductDef> findById(Long id) {
        return productDefJpaRepository.findById(id).map(productDefMapper::toDomain);
    }

    @Override
    public Optional<ProductDef> findByProductDefName(String productDefName) {
        return productDefJpaRepository.findByProductDefName(productDefName).map(productDefMapper::toDomain);
    }

    @Override
    public ProductDef save(ProductDef productDef) {
        // 1. Domain -> Entity 변환
        ProductDefEntity entity = productDefMapper.toEntity(productDef);
        // 2. JPA 리포지토리를 통해 DB에 저장
        ProductDefEntity savedEntity = productDefJpaRepository.save(entity);
        // 3. 저장된 Entity -> Domain 변환 후 반환
        return productDefMapper.toDomain(savedEntity);
    }

    @Override
    public List<ProductDef> save(List<ProductDef> productDefList) {
        List<ProductDef> result = new ArrayList<>();
        for(ProductDef  productDef : productDefList) {

            Optional<ProductDefEntity> optionalProductDefEntity = productDefJpaRepository.findByProductDefName(productDef.getProductDefName());

            if(optionalProductDefEntity.isPresent()) {
                ProductDefEntity entity = optionalProductDefEntity.get();
                ProductDefEntity savedEntity = productDefJpaRepository.save(entity);
                ProductDef savedDomain = productDefMapper.toDomain(savedEntity);
                result.add(savedDomain);
            }else{
                // 1. Domain -> Entity 변환
                ProductDefEntity entity = productDefMapper.toEntity(productDef);
                // 2. JPA 리포지토리를 통해 DB에 저장
                ProductDefEntity savedEntity = productDefJpaRepository.save(entity);
                // 3. 저장된 Entity -> Domain 변환 후 반환
                ProductDef savedDomain = productDefMapper.toDomain(savedEntity);
                result.add(savedDomain);
            }
        }

        return result;
    }

    @Override
    public void deleteAllByIdInBatch(List<Long> ids) {
        productDefJpaRepository.deleteAllByIdInBatch(ids);
    }
}
