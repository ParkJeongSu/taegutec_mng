package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.TableB;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TableBRepository extends JpaRepository<TableB, Long> {
}