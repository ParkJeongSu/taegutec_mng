package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.MenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MenuJpaRepository extends JpaRepository<MenuEntity, Long> {

    List<MenuEntity> findByFactoryNameOrderByMenuLevelAscDisplayOrderAsc(String factoryName);

    Optional<MenuEntity> findByFactoryNameAndMenuName(String factoryName, String menuName);
}
