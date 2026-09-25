package kr.co.aim.domain.repository;

import kr.co.aim.domain.model.Menu;

import java.util.List;
import java.util.Optional;

public interface MenuRepository {

    List<Menu> findAll();

    Optional<Menu> findById(Long id);

    List<Menu> findByFactoryNameOrderByMenuLevelAscDisplayOrderAsc(String factoryName);

    Optional<Menu> findByFactoryNameAndMenuName(String factoryName, String menuName);

    Menu save(Menu menu);

    void deleteById(Long id);

    void deleteAllByIdInBatch(List<Long> ids);

    List<Menu> findAuthorizedMenusByUserId(String userId);
}
