package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.SysUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SysUserJpaRepository extends JpaRepository<SysUserEntity, Long> {

    Optional<SysUserEntity> findByFactoryNameAndUserId(String factoryName, String userId);

    List<SysUserEntity> findByFactoryName(String factoryName);
}
