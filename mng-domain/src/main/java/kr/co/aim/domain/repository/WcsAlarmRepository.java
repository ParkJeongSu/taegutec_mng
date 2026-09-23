package kr.co.aim.domain.repository;

import kr.co.aim.common.condition.WcsAlarmSearchCondition;
import kr.co.aim.domain.model.WcsAlarm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface WcsAlarmRepository {

    List<WcsAlarm> findAll();

    Optional<WcsAlarm> findById(String factoryName, String equipmentName, String alarmId, Integer layerNumber, String layerType);

    List<WcsAlarm> findByFactoryNameAndEquipmentName(String factoryName, String equipmentName);

    boolean existsById(String factoryName, String equipmentName, String alarmId, Integer layerNumber, String layerType);

    WcsAlarm save(WcsAlarm alarm);

    void deleteById(String factoryName, String equipmentName, String alarmId, Integer layerNumber, String layerType);

    Page<WcsAlarm> findAlarms(WcsAlarmSearchCondition condition, Pageable pageable);
}
