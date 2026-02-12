package kr.co.aim.domain.repository;

import kr.co.aim.domain.model.TaskJobDetail;

import java.util.List;
import java.util.Optional;

/**
 * 사용자 저장소의 기능을 정의하는 인터페이스.
 * 애플리케이션의 다른 부분(서비스 계층 등)은 이 인터페이스에만 의존합니다.
 * 실제 구현 기술(JPA, JDBC 등)과는 완전히 분리됩니다.
 */
public interface TaskJobDetailRepository {

    TaskJobDetail save(TaskJobDetail taskJobDetail);

    Optional<TaskJobDetail> findById(Long id);

    List<TaskJobDetail> findAll();

    void deleteAllByIdInBatch(List<Long>ids);

    List<TaskJobDetail> findByWipName(String wipName);

    List<TaskJobDetail> findByCarrierNameAndState(String carrierName,String state);

//    Page<TaskJobDetailResponseDto> findTaskJobDetailWithConditions(TaskJobDetailSearchConditionDto condition, Pageable pageable);
}
