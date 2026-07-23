package kr.co.aim.api.service;

import kr.co.aim.common.dto.CarrierDefSaveRequestDto;
import kr.co.aim.common.condition.CarrierDefSearchCondition;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.CarrierDefCreateCommand;
import kr.co.aim.domain.command.CarrierDefUpdateCommand;
import kr.co.aim.domain.model.CarrierDef;
import kr.co.aim.domain.repository.CarrierDefRepository;
import kr.co.aim.infra.persistence.entity.CarrierDefHistoryEntity;
import kr.co.aim.infra.persistence.mapper.CarrierDefMapper;
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
public class CarrierDefService {
    private final CarrierDefRepository carrierDefRepository;
    private final HistoryService historyService;
    private final CarrierDefMapper carrierDefMapper;

    // [Create] 기준정보 등록
    @Transactional
    public CarrierDef createCarrierDef(CarrierDefSaveRequestDto dto) {
        // 중복 검증
        Optional<CarrierDef> existing = carrierDefRepository.findByCarrierDefName(dto.getCarrierDefName());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 Carrier Def Name입니다: " + dto.getCarrierDefName());
        }
        TransactionInfo tx = TransactionInfo.now(dto.getEventName(),dto.getEventUser(),dto.getEventComment());
        CarrierDefCreateCommand command =
                CarrierDefCreateCommand
                        .builder()
                        .transactionInfo(tx)
                        .carrierDefName(dto.getCarrierDefName())
                        .factoryName(dto.getFactoryName())
                        .description(dto.getDescription())
                        .carrierType(dto.getCarrierType())
                        .carrierDetailType(dto.getCarrierDetailType())
                        .defaultCapacity(dto.getDefaultCapacity())
                        .useCountLimit(dto.getUseCountLimit())
                        .useDurationLimit(dto.getUseDurationLimit())
                        .countLimitPerClean(dto.getCountLimitPerClean())
                        .durationLimitPerClean(dto.getDurationLimitPerClean())
                        .cleanCountLimit(dto.getCleanCountLimit())
                        .checkOutState(dto.getCheckOutState())
                        .checkOutTime(dto.getCheckOutTime())
                        .checkOutUser(dto.getCheckOutUser())
                        .dataState(dto.getDataState())
                        .eventName(dto.getEventName())
                        .eventTime(dto.getEventTime())
                        .eventUser(dto.getEventUser())
                        .eventComment(dto.getEventComment())
                        .build();

        CarrierDef carrierDef = CarrierDef.create(command);
        carrierDef = carrierDefRepository.save(carrierDef);
        CarrierDefHistoryEntity historyEntity = carrierDefMapper.toHistoryEntity(carrierDef);
        historyService.saveHistory(historyEntity);
        return carrierDef;
    }

    // [Read] Querydsl 동적 조건 페이징 조회
    public Page<CarrierDef> findCarrierDefWithConditions(CarrierDefSearchCondition condition, Pageable pageable) {
        return carrierDefRepository.findCarrierDefWithConditions(condition, pageable);
    }

    // [Read] 단건 상세 조회
    public CarrierDef findById(Long id) {
        Optional<CarrierDef> carrierDefOptional = carrierDefRepository.findById(id);
        if (carrierDefOptional.isEmpty()) {
            throw new IllegalArgumentException("해당 기준정보가 존재하지 않습니다. ID: " + id);
        }
        return carrierDefOptional.get();
    }

    // [Update] 기준정보 수정
    @Transactional
    public CarrierDef updateCarrierDef(CarrierDefSaveRequestDto dto) {
        Optional<CarrierDef> carrierDefOptional = carrierDefRepository.findById(dto.getId());
        if (carrierDefOptional.isEmpty()) {
            throw new IllegalArgumentException("수정할 대상 기준정보가 없습니다. ID: " + dto.getId());
        }

        CarrierDef carrierDef = carrierDefOptional.get();

        TransactionInfo tx = TransactionInfo.now(dto.getEventName(),dto.getEventUser(),dto.getEventComment());
        CarrierDefUpdateCommand command =
                CarrierDefUpdateCommand
                        .builder()
                        .transactionInfo(tx)
                        .carrierDefName(dto.getCarrierDefName())
                        .factoryName(dto.getFactoryName())
                        .description(dto.getDescription())
                        .carrierType(dto.getCarrierType())
                        .carrierDetailType(dto.getCarrierDetailType())
                        .defaultCapacity(dto.getDefaultCapacity())
                        .useCountLimit(dto.getUseCountLimit())
                        .useDurationLimit(dto.getUseDurationLimit())
                        .countLimitPerClean(dto.getCountLimitPerClean())
                        .durationLimitPerClean(dto.getDurationLimitPerClean())
                        .cleanCountLimit(dto.getCleanCountLimit())
                        .checkOutState(dto.getCheckOutState())
                        .checkOutTime(dto.getCheckOutTime())
                        .checkOutUser(dto.getCheckOutUser())
                        .dataState(dto.getDataState())
                        .build();

        carrierDef.update(command);
        carrierDef = carrierDefRepository.save(carrierDef);
        CarrierDefHistoryEntity historyEntity = carrierDefMapper.toHistoryEntity(carrierDef);
        historyService.saveHistory(historyEntity);
        return carrierDef;
    }

    // [Delete] 배치 벌크 삭제 처리
    @Transactional
    public void deleteCarrierDefs(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        carrierDefRepository.deleteAllByIdInBatch(ids);
    }

}