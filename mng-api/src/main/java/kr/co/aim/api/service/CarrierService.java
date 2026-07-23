package kr.co.aim.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.aim.common.condition.CarrierLotSearchCondition;
import kr.co.aim.common.condition.CarrierSearchCondition;
import kr.co.aim.common.dto.CarrierLotSearchResultDto;
import kr.co.aim.common.enums.*;
import kr.co.aim.common.format.*;
import kr.co.aim.domain.model.*;
import kr.co.aim.domain.repository.*;
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
public class CarrierService {
    private final ObjectMapper objectMapper;
    private final CarrierDefRepository carrierDefRepository;
    private final CarrierRepository carrierRepository;

    @Transactional(value = "mssqlTransactionManager")
    public Optional<Carrier> findByCarrierName(String carrierName){
        return carrierRepository.findByCarrierName(carrierName);
    }

    @Transactional(value = "mssqlTransactionManager")
    public Page<Carrier> findCarrierByCondition(CarrierSearchCondition condition,Pageable pageable){
        return carrierRepository.findCarrierByCondition(condition,pageable);
    }

    @Transactional(value = "mssqlTransactionManager")
    public Page<CarrierLotSearchResultDto> findCarrierLotByCondition(CarrierLotSearchCondition condition, Pageable pageable){
        return carrierRepository.findCarrierLotByCondition(condition,pageable);
    }

    @Transactional(value = "mssqlTransactionManager")
    public Carrier save(Carrier carrier){
        return carrierRepository.save(carrier);
    }


    @Transactional(value = "mssqlTransactionManager")
    public void deleteAllCarriersByIdInBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return; // 삭제할 ID가 없으면 아무 작업도 하지 않음
        }
        // 여러 건을 삭제할 때는 이 메서드가 성능상 가장 효율적입니다.
        // DELETE ... WHERE id IN (...) 쿼리를 한 번에 실행합니다.
        carrierRepository.deleteAllByIdInBatch(ids);
    }

    @Transactional(value = "mssqlTransactionManager")
    public List<Carrier> findCarriersForEmptyContainer (String cleanState,
                                                        String transportState,
                                                        String transportJobId,
                                                        String useState,
                                                        Integer quantity,
                                                        List<String> containerTypes){
        return carrierRepository.findCarriersForEmptyContainer(
                cleanState,
                transportState,
                transportJobId,
                useState,
                quantity,
                containerTypes
        );
    }

}