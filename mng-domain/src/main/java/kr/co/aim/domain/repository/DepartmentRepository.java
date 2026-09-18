package kr.co.aim.domain.repository;

import kr.co.aim.domain.model.Department;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepository {

    List<Department> findAll();

    Optional<Department> findById(Long id);

    Optional<Department> findByFactoryNameAndDepartmentName(String factoryName, String departmentName);

    List<Department> findByFactoryName(String factoryName);

    Department save(Department department);

    void deleteById(Long id);

    void deleteAllByIdInBatch(List<Long> ids);
}
