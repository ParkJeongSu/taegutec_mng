package kr.co.aim.domain.repository;

import kr.co.aim.domain.model.AlarmAction;

import java.util.List;
import java.util.Optional;

/**
 * 알람 저장소의 기능을 정의하는 인터페이스.
 * 애플리케이션의 다른 부분(서비스 계층 등)은 이 인터페이스에만 의존합니다.
 * 실제 구현 기술(JPA, JDBC 등)과는 완전히 분리됩니다.
 */
public interface AlarmActionRepository {
    /**
     * 모든 알람정의을 찾습니다.
     * @return 모든 알람액션 도메인 객체 리스트
     */
    List<AlarmAction> findAll();

    /**
     * ID로 사용자를 찾습니다.
     * @param id 알람 ID
     * @return Optional<AlarmAction>
     */
    Optional<AlarmAction> findById(Long id);

    /**
     * ID로 사용자를 찾습니다.
     * @param alarmDefId 알람 정의 ID
     * @return Optional<AlarmAction>
     */
    List<AlarmAction> findByAlarmDefId(Long alarmDefId);

    Optional<AlarmAction> findByAlarmActionName(String alarmActionName);

    /**
     * 알람정의를 저장하거나 업데이트합니다.
     * @param alarmAction 저장할 알람정의 도메인 객체
     * @return 저장된 알람액션 도메인 객체 (ID 포함)
     */
    AlarmAction save(AlarmAction alarmAction);


//    Page<AlarmActionResponseDto> findAlarmActionWithConditions(AlarmActionSearchConditionDto condition, Pageable pageable);

    void deleteAllByIdInBatch(List<Long>ids);
}
