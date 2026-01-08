package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.TableA;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TableARepository extends JpaRepository<TableA, Long> {
}