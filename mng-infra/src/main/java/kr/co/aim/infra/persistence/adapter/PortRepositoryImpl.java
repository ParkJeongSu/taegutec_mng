package kr.co.aim.infra.persistence.adapter;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.domain.model.Port;
import kr.co.aim.domain.repository.PortRepository;
import kr.co.aim.infra.persistence.entity.PortEntity;
import kr.co.aim.infra.persistence.mapper.PortMapper;
import kr.co.aim.infra.persistence.springdatajpa.PortJpaRepository;
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
public class PortRepositoryImpl implements PortRepository {
    // Spring Data JPA가 자동으로 구현해주는 JPA 리포지토리. UserEntity를 다룬다.
    private final PortJpaRepository portJpaRepository;
    private final PortMapper portMapper;
    private final JPAQueryFactory queryFactory; // ✨ JPAQueryFactory 주입


    @Override
    public Port save(Port port) {
        // 1. Domain -> Entity 변환
        PortEntity entity = portMapper.toEntity(port);
        // 2. JPA 리포지토리를 통해 DB에 저장
        PortEntity savedEntity = portJpaRepository.save(entity);
        // 3. 저장된 Entity -> Domain 변환 후 반환
        return portMapper.toDomain(savedEntity);
    }

    @Override
    public List<Port> findAll() {
        return portJpaRepository.findAll().stream().map(portMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<Port> findById(Long id) {
        return portJpaRepository.findById(id).map(portMapper::toDomain);
    }

    @Override
    public Optional<Port> findByEquipmentNameAndPortName(String equipmentName, String portName) {
        return portJpaRepository.findByEquipmentNameAndPortName(equipmentName,portName).map(portMapper::toDomain);
    }

    @Override
    public Optional<Port> findWithLockByEquipmentNameAndPortName(String equipmentName, String portName) {
        return portJpaRepository.findWithLockByEquipmentNameAndPortName(equipmentName,portName).map(portMapper::toDomain);
    }

    @Override
    public List<Port> findByTransportState(String transportState) {
        return portJpaRepository.findByTransportState(transportState).stream().map(portMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Port> findEarliestPortPerWorkCenter(String transportState) {
        return portJpaRepository.findEarliestPortPerWorkCenter(transportState).stream().map(portMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteAllByIdInBatch(List<Long> ids) {
        portJpaRepository.deleteAllByIdInBatch(ids);
    }

    @Override
    public List<Port> findByTransportStateAndPortRoleType(String transportState, String portRoleType) {
        return portJpaRepository.findByTransportStateAndPortRoleType(transportState,portRoleType).stream().map(portMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Port> findByTransportStateAndDetailPortTypeIn(String transportState, List<String> detailPortType) {
        return portJpaRepository.findByTransportStateAndDetailPortTypeIn(transportState,detailPortType).stream().map(portMapper::toDomain).collect(Collectors.toList());
    }

}
