package kr.co.aim.api.service;

import kr.co.aim.api.dto.SystemDefCreateRequestDto;
import kr.co.aim.api.dto.SystemDefResponseDto;
import kr.co.aim.api.dto.SystemDefSearchConditionDto;
import kr.co.aim.api.dto.SystemDefUpdateRequestDto;
import kr.co.aim.common.enums.EventName;
import kr.co.aim.common.error.EntityExistException;
import kr.co.aim.common.error.EntityNotFoundException;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.SystemDefCreateCommand;
import kr.co.aim.domain.command.SystemDefUpdateCommand;
import kr.co.aim.domain.model.SystemDef;
import kr.co.aim.domain.repository.SystemDefRepository;
import kr.co.aim.infra.persistence.mapper.SystemDefMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 만들어줍니다. (DI)
public class SystemDefService {

    private final SystemDefRepository systemDefRepository; // 구현체(Infra)가 아닌 인터페이스(Domain)에 의존
    private final SystemDefMapper systemDefMapper;

    /**
     * 사용자의 데이터를 생성합니다.
     * @param requestDto 사용자의 생성 데이터
     * @return 생성된 사용자 도메인 객체
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public SystemDef createSystemDef(SystemDefCreateRequestDto requestDto) {
        // 1. Repository를 통해 Domain 객체를 가져온다.
        Optional<SystemDef> optionalSystemDef = systemDefRepository.findBySystemDefName(requestDto.getSystemDefName());
        if(optionalSystemDef.isPresent()){
            throw new EntityExistException("이미 생성된 시스템입니다. ID: " + requestDto.getSystemDefName());
        }

        String eventName = EventName.CREATED.getValue();

        TransactionInfo tx = TransactionInfo.now(eventName,requestDto.getEventUser(),requestDto.getEventComment());
        SystemDefCreateCommand command =
                SystemDefCreateCommand.builder()
                        .systemDefName(requestDto.getSystemDefName())
                        .transactionInfo(tx)
                        .build();

        SystemDef systemDef = SystemDef.create(command);

        return systemDefRepository.save(systemDef);
    }

    @Transactional(readOnly = true)
    public Page<SystemDefResponseDto> findSystemDefs(SystemDefSearchConditionDto condition,Pageable pageable) {
        //1. Repository에서 Page<Entity>를 조회합니다.

        Page<SystemDefResponseDto> page = null;//systemDefRepository.findSystemDefWithConditions(condition,pageable);

        return page;
    }

    /**
     * 사용자의 데이터를 변경합니다.
     * @param requestDto 사용자의 변경 데이터
     * @return 변경된 사용자 도메인 객체
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public SystemDef changeSystemDef(Long id, SystemDefUpdateRequestDto requestDto) {
        // 1. Repository를 통해 Domain 객체를 가져온다.
        SystemDef systemDef;
        Optional<SystemDef> optionalSystemDef = systemDefRepository.findById(id);
        if(optionalSystemDef.isPresent()){
            systemDef = optionalSystemDef.get();
        }
        else {
            throw new EntityNotFoundException("존재하지 않는 설정입니다. ID: " + requestDto.getId());
        }
        String eventName = EventName.UPDATED.getValue();

        TransactionInfo tx = TransactionInfo.now(eventName,requestDto.getEventUser(),requestDto.getEventComment());
        SystemDefUpdateCommand command =
                SystemDefUpdateCommand.builder()
                        .transactionInfo(tx)
                        .build();

        systemDef.changeSystemDef(command);

        return systemDefRepository.save(systemDef);
    }


    @Transactional
    public void deleteAllByIdInBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return; // 삭제할 ID가 없으면 아무 작업도 하지 않음
        }
        // 여러 건을 삭제할 때는 이 메서드가 성능상 가장 효율적입니다.
        // DELETE ... WHERE id IN (...) 쿼리를 한 번에 실행합니다.
        systemDefRepository.deleteAllByIdInBatch(ids);
    }

}