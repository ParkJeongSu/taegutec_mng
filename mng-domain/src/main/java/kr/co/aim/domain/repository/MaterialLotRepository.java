package kr.co.aim.domain.repository;

import kr.co.aim.domain.model.MaterialLot;
import kr.co.aim.domain.repository.condition.MaterialLotSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface MaterialLotRepository {

    List<MaterialLot> findAll();

    Optional<MaterialLot> findById(Long id);

    Optional<MaterialLot> findByMaterialLotId(String materialLotId);

    MaterialLot save(MaterialLot materialLot);

    void deleteById(Long id);

    void deleteAllByIdInBatch(List<Long> ids);

    Page<MaterialLot> findMaterialLotsWithConditions(MaterialLotSearchCondition condition, Pageable pageable);
}
