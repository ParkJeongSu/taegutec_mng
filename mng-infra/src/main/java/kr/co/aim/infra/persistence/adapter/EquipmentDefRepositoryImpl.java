package kr.co.aim.infra.persistence.adapter;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.domain.model.EquipmentDef;
import kr.co.aim.domain.repository.EquipmentDefRepository;
import kr.co.aim.infra.persistence.entity.EquipmentDefEntity;
import kr.co.aim.infra.persistence.mapper.EquipmentDefMapper;
import kr.co.aim.infra.persistence.springdatajpa.EquipmentDefJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
/**
 * UserRepository의 JPA 기반 구현체.
 * 실제 DB 작업은 Spring Data JPA가 제공하는 JpaRepository에 위임합니다.
 */

@Repository
@RequiredArgsConstructor
public class EquipmentDefRepositoryImpl implements EquipmentDefRepository {
    // Spring Data JPA가 자동으로 구현해주는 JPA 리포지토리. UserEntity를 다룬다.

    private final EquipmentDefJpaRepository equipmentDefJpaRepository;
    private final EquipmentDefMapper equipmentDefMapper;
    private final JPAQueryFactory queryFactory; // ✨ JPAQueryFactory 주입


    @Override
    public List<EquipmentDef> findAll() {
        return equipmentDefJpaRepository.findAll().stream().map(equipmentDefMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<EquipmentDef> findByEquipmentName(String equipmentName) {
        return equipmentDefJpaRepository.findByEquipmentName(equipmentName).map(equipmentDefMapper::toDomain);
    }

    @Override
    public EquipmentDef save(EquipmentDef equipmentDef) {
        EquipmentDefEntity entity = equipmentDefMapper.toEntity(equipmentDef);
        EquipmentDefEntity savedEntity = equipmentDefJpaRepository.save(entity);
        return equipmentDefMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteAllByIdInBatch(List<Long> ids) {
        equipmentDefJpaRepository.deleteAllByIdInBatch(ids);
    }
}
