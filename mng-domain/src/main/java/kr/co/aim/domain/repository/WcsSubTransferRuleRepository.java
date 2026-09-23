package kr.co.aim.domain.repository;

import kr.co.aim.common.condition.WcsSubTransferRuleSearchCondition;
import kr.co.aim.domain.model.WcsSubTransferRule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface WcsSubTransferRuleRepository {

    List<WcsSubTransferRule> findAll();

    Optional<WcsSubTransferRule> findById(String factoryName, String equipmentName, String moduleName, Long routeLinkId);

    List<WcsSubTransferRule> findByFactoryName(String factoryName);

    List<WcsSubTransferRule> findByFactoryNameAndEquipmentName(String factoryName, String equipmentName);

    boolean existsById(String factoryName, String equipmentName, String moduleName, Long routeLinkId);

    WcsSubTransferRule save(WcsSubTransferRule subTransferRule);

    void deleteById(String factoryName, String equipmentName, String moduleName, Long routeLinkId);

    Page<WcsSubTransferRule> findSubTransferRules(WcsSubTransferRuleSearchCondition condition, Pageable pageable);
}
