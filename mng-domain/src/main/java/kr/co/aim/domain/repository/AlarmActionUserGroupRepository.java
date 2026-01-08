package kr.co.aim.domain.repository;

//import kr.co.aim.domain.model.AlarmAction;

import kr.co.aim.common.dto.AlarmActionResponseDto;
import kr.co.aim.common.dto.AlarmActionSearchConditionDto;
import kr.co.aim.common.dto.AlarmActionUserGroupResponseDto;
import kr.co.aim.common.dto.AlarmActionUserGroupSearchConditionDto;
import kr.co.aim.domain.model.AlarmActionUserGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 알람 저장소의 기능을 정의하는 인터페이스.
 * 애플리케이션의 다른 부분(서비스 계층 등)은 이 인터페이스에만 의존합니다.
 * 실제 구현 기술(JPA, JDBC 등)과는 완전히 분리됩니다.
 */
public interface AlarmActionUserGroupRepository {
    /**
     * 모든 알람정의을 찾습니다.
     * @return 모든 알람액션 도메인 객체 리스트
     */
    List<AlarmActionUserGroup> findAll();

    /**
     * ID로 사용자를 찾습니다.
     * @param id 알람 ID
     * @return Optional<AlarmAction>
     */
    Optional<AlarmActionUserGroup> findById(Long id);


    /**
     * 알람정의를 저장하거나 업데이트합니다.
     * @param alarmAction 저장할 알람정의 도메인 객체
     * @return 저장된 알람액션 도메인 객체 (ID 포함)
     */
    AlarmActionUserGroup save(AlarmActionUserGroup alarmAction);

    /**
     * ID로 사용자를 찾습니다.
     * @param userGroupName 알람 ID
     * @return Optional<AlarmAction>
     */
    Optional<AlarmActionUserGroup> findByUserGroupName(String userGroupName);

    Page<AlarmActionUserGroupResponseDto> findAlarmUserGroupWithConditions(AlarmActionUserGroupSearchConditionDto condition, Pageable pageable);

    void deleteAllByIdInBatch(List<Long>ids);
}
