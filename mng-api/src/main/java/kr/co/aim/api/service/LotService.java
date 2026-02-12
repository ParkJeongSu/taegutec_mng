package kr.co.aim.api.service;

import kr.co.aim.api.dto.LotsCreateRequestDto;
import kr.co.aim.api.dto.LotsResponseDto;
import kr.co.aim.api.dto.LotsSearchConditionDto;
import kr.co.aim.api.dto.LotsUpdateRequestDto;
import kr.co.aim.common.enums.EventName;
import kr.co.aim.common.error.EntityExistException;
import kr.co.aim.common.error.EntityNotFoundException;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.LotsCreateCommand;
import kr.co.aim.domain.command.LotsUpdateCommand;
import kr.co.aim.domain.model.Lots;
import kr.co.aim.domain.repository.LotsRepository;
import kr.co.aim.infra.persistence.mapper.LotsMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 만들어줍니다. (DI)
@Slf4j
public class LotService {
    private final LotsRepository lotsRepository;
    private final LotsMapper lotsMapper;

    /**
     * 사용자의 데이터를 생성합니다.
     * @param requestDto 사용자의 생성 데이터
     * @return 생성된 사용자 도메인 객체
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public Lots createLots(LotsCreateRequestDto requestDto) {
        // 1. Repository를 통해 Domain 객체를 가져온다.
        Optional<Lots> optionalLots = lotsRepository.findByLotName(requestDto.getLotName());
        if(optionalLots.isPresent()){
            throw new EntityExistException("이미 생성된 Lot 이름입니다. ID: " + requestDto.getLotName());
        }

        String eventName = EventName.CREATED.getValue();

        TransactionInfo tx = TransactionInfo.now(eventName,requestDto.getEventUser(),requestDto.getEventComment());
        LotsCreateCommand command =
                LotsCreateCommand.builder()
                        .lotName(requestDto.getLotName())
                        .transactionInfo(tx)
                        .build();

        Lots lots = Lots.create(command);

        return lotsRepository.save(lots);
    }

    @Transactional(readOnly = true)
    public Page<LotsResponseDto> findLots(LotsSearchConditionDto condition, Pageable pageable) {
        //1. Repository에서 Page<Entity>를 조회합니다.

        Page<LotsResponseDto> page = null;//lotsRepository.findLotsWithConditions(condition,pageable);

        return page;
    }

    /**
     * 사용자의 데이터를 변경합니다.
     * @param requestDto 사용자의 변경 데이터
     * @return 변경된 사용자 도메인 객체
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public Lots changeLots(Long id, LotsUpdateRequestDto requestDto) {
        // 1. Repository를 통해 Domain 객체를 가져온다.
        Lots lots;
        Optional<Lots> optionalLots = lotsRepository.findById(id);
        if(optionalLots.isPresent()){
            lots = optionalLots.get();
        }
        else {
            throw new EntityNotFoundException("존재하지 않는 설정입니다. ID: " + requestDto.getId());
        }
        String eventName = EventName.UPDATED.getValue();

        TransactionInfo tx = TransactionInfo.now(eventName,requestDto.getEventUser(),requestDto.getEventComment());
        LotsUpdateCommand command =
                LotsUpdateCommand.builder()
                        .transactionInfo(tx)
                        .build();

        lots.changeLots(command);

        return lotsRepository.save(lots);
    }


    @Transactional
    public void deleteAllByIdInBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return; // 삭제할 ID가 없으면 아무 작업도 하지 않음
        }
        // 여러 건을 삭제할 때는 이 메서드가 성능상 가장 효율적입니다.
        // DELETE ... WHERE id IN (...) 쿼리를 한 번에 실행합니다.
        lotsRepository.deleteAllByIdInBatch(ids);
    }
}
