package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.SystemDefEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Spring Data JPA가 UserEntity 객체를 위해 자동으로 구현할 인터페이스.
 * UserRepositoryImpl 내부에서만 사용됩니다.
 */
public interface SystemDefJpaRepository extends JpaRepository<SystemDefEntity, Long> {
    Optional<SystemDefEntity> findBySystemDefName(String systemDefName);
}