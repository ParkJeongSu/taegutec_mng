package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.InsertAutoTestResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InsertAutoTestResultJpaRepository extends JpaRepository<InsertAutoTestResultEntity, Long> {

    List<InsertAutoTestResultEntity> findByAutoTestId(Long autoTestId);

    void deleteByAutoTestId(Long autoTestId);
}
