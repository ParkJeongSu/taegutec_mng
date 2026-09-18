package kr.co.aim.infra.persistence.adapter;

import kr.co.aim.domain.model.Department;
import kr.co.aim.domain.repository.DepartmentRepository;
import kr.co.aim.infra.persistence.entity.DepartmentEntity;
import kr.co.aim.infra.persistence.mapper.DepartmentMapper;
import kr.co.aim.infra.persistence.springdatajpa.DepartmentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DepartmentRepository의 JPA 기반 어댑터 구현체.
 * 실제 DB 작업은 Spring Data JPA가 제공하는 DepartmentJpaRepository에 위임합니다.
 */
@Repository
@RequiredArgsConstructor
public class DepartmentRepositoryAdapter implements DepartmentRepository {

    private final DepartmentJpaRepository departmentJpaRepository;
    private final DepartmentMapper departmentMapper;

    @Override
    public List<Department> findAll() {
        List<DepartmentEntity> entities = departmentJpaRepository.findAll();
        List<Department> result = new ArrayList<>();
        for (DepartmentEntity entity : entities) {
            result.add(departmentMapper.toDomain(entity));
        }
        return result;
    }

    @Override
    public Optional<Department> findById(Long id) {
        Optional<DepartmentEntity> entityOptional = departmentJpaRepository.findById(id);
        if (entityOptional.isPresent()) {
            return Optional.ofNullable(departmentMapper.toDomain(entityOptional.get()));
        }
        return Optional.empty();
    }

    @Override
    public Optional<Department> findByFactoryNameAndDepartmentName(String factoryName, String departmentName) {
        Optional<DepartmentEntity> entityOptional = departmentJpaRepository.findByFactoryNameAndDepartmentName(factoryName, departmentName);
        if (entityOptional.isPresent()) {
            return Optional.ofNullable(departmentMapper.toDomain(entityOptional.get()));
        }
        return Optional.empty();
    }

    @Override
    public List<Department> findByFactoryName(String factoryName) {
        List<DepartmentEntity> entities = departmentJpaRepository.findByFactoryName(factoryName);
        List<Department> result = new ArrayList<>();
        for (DepartmentEntity entity : entities) {
            result.add(departmentMapper.toDomain(entity));
        }
        return result;
    }

    @Override
    public Department save(Department department) {
        DepartmentEntity entity = departmentMapper.toEntity(department);
        DepartmentEntity savedEntity = departmentJpaRepository.save(entity);
        return departmentMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(Long id) {
        departmentJpaRepository.deleteById(id);
    }

    @Override
    public void deleteAllByIdInBatch(List<Long> ids) {
        departmentJpaRepository.deleteAllByIdInBatch(ids);
    }
}
