package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.TaskJobDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskJobDetailJpaRepository extends JpaRepository<TaskJobDetailEntity, Long> {
    List<TaskJobDetailEntity> findByWipName(String wipName);
    List<TaskJobDetailEntity> findByCarrierNameAndState(String carrierName,String state);
}