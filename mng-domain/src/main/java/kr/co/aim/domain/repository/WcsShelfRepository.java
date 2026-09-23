package kr.co.aim.domain.repository;

import kr.co.aim.common.condition.WcsShelfSearchCondition;
import kr.co.aim.domain.model.WcsShelf;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface WcsShelfRepository {

    List<WcsShelf> findAll();

    Optional<WcsShelf> findById(String factoryName, String stockerName, String shelfName);

    List<WcsShelf> findByFactoryNameAndStockerName(String factoryName, String stockerName);

    boolean existsById(String factoryName, String stockerName, String shelfName);

    WcsShelf save(WcsShelf shelf);

    void deleteById(String factoryName, String stockerName, String shelfName);

    Page<WcsShelf> findShelves(WcsShelfSearchCondition condition, Pageable pageable);
}
