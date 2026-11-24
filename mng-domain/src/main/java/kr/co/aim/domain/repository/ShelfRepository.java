package kr.co.aim.domain.repository;

import kr.co.aim.domain.model.Shelf;
import kr.co.aim.domain.repository.condition.ShelfSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ShelfRepository {

    List<Shelf> findAll();

    Optional<Shelf> findById(Long id);

    Optional<Shelf> findByLocationId(String locationId);

    Shelf save(Shelf shelf);

    void deleteById(Long id);

    void deleteAllByIdInBatch(List<Long> ids);

    Page<Shelf> findShelfsWithConditions(ShelfSearchCondition condition, Pageable pageable);
}
