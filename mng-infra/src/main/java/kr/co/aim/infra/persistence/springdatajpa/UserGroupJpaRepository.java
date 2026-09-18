package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.UserGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserGroupJpaRepository extends JpaRepository<UserGroupEntity, Long> {

    Optional<UserGroupEntity> findByFactoryNameAndUserGroupName(String factoryName, String userGroupName);

    List<UserGroupEntity> findByFactoryName(String factoryName);
}
