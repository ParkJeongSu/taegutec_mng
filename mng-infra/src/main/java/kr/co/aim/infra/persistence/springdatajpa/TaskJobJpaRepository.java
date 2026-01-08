package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.TaskJobEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaskJobJpaRepository extends JpaRepository<TaskJobEntity, Long> {
    Optional<TaskJobEntity> findByEquipmentName(String equipmentName);

    Optional<TaskJobEntity> findByTaskState(String taskState);
}
