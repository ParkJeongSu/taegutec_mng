package kr.co.aim.infra.persistence.adapter;

import kr.co.aim.domain.model.Menu;
import kr.co.aim.domain.repository.MenuRepository;
import kr.co.aim.infra.persistence.entity.MenuEntity;
import kr.co.aim.infra.persistence.mapper.MenuMapper;
import kr.co.aim.infra.persistence.springdatajpa.MenuJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * MenuRepository의 JPA 기반 어댑터 구현체.
 * 실제 DB 작업은 Spring Data JPA가 제공하는 MenuJpaRepository에 위임합니다.
 */
@Repository
@RequiredArgsConstructor
public class MenuRepositoryAdapter implements MenuRepository {

    private final MenuJpaRepository menuJpaRepository;
    private final MenuMapper menuMapper;

    @Override
    public List<Menu> findAll() {
        List<MenuEntity> entities = menuJpaRepository.findAll();
        List<Menu> result = new ArrayList<>();
        for (MenuEntity entity : entities) {
            result.add(menuMapper.toDomain(entity));
        }
        return result;
    }

    @Override
    public Optional<Menu> findById(Long id) {
        Optional<MenuEntity> entityOptional = menuJpaRepository.findById(id);
        if (entityOptional.isPresent()) {
            return Optional.ofNullable(menuMapper.toDomain(entityOptional.get()));
        }
        return Optional.empty();
    }

    @Override
    public List<Menu> findByFactoryNameOrderByMenuLevelAscDisplayOrderAsc(String factoryName) {
        List<MenuEntity> entities = menuJpaRepository.findByFactoryNameOrderByMenuLevelAscDisplayOrderAsc(factoryName);
        List<Menu> result = new ArrayList<>();
        for (MenuEntity entity : entities) {
            result.add(menuMapper.toDomain(entity));
        }
        return result;
    }

    @Override
    public Optional<Menu> findByFactoryNameAndMenuName(String factoryName, String menuName) {
        Optional<MenuEntity> entityOptional = menuJpaRepository.findByFactoryNameAndMenuName(factoryName, menuName);
        if (entityOptional.isPresent()) {
            return Optional.ofNullable(menuMapper.toDomain(entityOptional.get()));
        }
        return Optional.empty();
    }

    @Override
    public Menu save(Menu menu) {
        MenuEntity entity = menuMapper.toEntity(menu);
        MenuEntity savedEntity = menuJpaRepository.save(entity);
        return menuMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(Long id) {
        menuJpaRepository.deleteById(id);
    }

    @Override
    public void deleteAllByIdInBatch(List<Long> ids) {
        menuJpaRepository.deleteAllByIdInBatch(ids);
    }
}
