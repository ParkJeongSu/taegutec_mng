package kr.co.aim.infra.persistence.adapter;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.domain.model.EquipmentGroup;
import kr.co.aim.domain.repository.EquipmentGroupRepository;
import kr.co.aim.infra.persistence.entity.EquipmentGroupEntity;
import kr.co.aim.infra.persistence.mapper.EquipmentGroupMapper;
import kr.co.aim.infra.persistence.springdatajpa.EquipmentGroupJpaRepository;
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
public class EquipmentGroupRepositoryImpl implements EquipmentGroupRepository {
    // Spring Data JPA가 자동으로 구현해주는 JPA 리포지토리. UserEntity를 다룬다.
    private final EquipmentGroupJpaRepository equipmentGroupJpaRepository;
    private final EquipmentGroupMapper equipmentGroupMapper;
    private final JPAQueryFactory queryFactory; // ✨ JPAQueryFactory 주입

    @Override
    public void deleteAllByIdInBatch(List<Long> ids) {
        equipmentGroupJpaRepository.deleteAllByIdInBatch(ids);
    }

    @Override
    public List<EquipmentGroup> findAll() {
        return equipmentGroupJpaRepository.findAll().stream().map(equipmentGroupMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public EquipmentGroup save(EquipmentGroup equipmentGroup) {
        EquipmentGroupEntity entity = equipmentGroupMapper.toEntity(equipmentGroup);
        EquipmentGroupEntity savedEntity = equipmentGroupJpaRepository.save(entity);
        return equipmentGroupMapper.toDomain(savedEntity);
    }

}
