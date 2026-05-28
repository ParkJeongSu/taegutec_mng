package kr.co.aim.api.service;

import kr.co.aim.common.condition.GALDetailInterfaceSearchCondition;
import kr.co.aim.common.condition.GALInterfaceSearchCondition;
import kr.co.aim.api.strategy.FactoryGALInterfaceStrategy;
import kr.co.aim.common.condition.GALPartSearchCondition;
import kr.co.aim.common.dto.powder.IdocH2PartMResponseDto;
import kr.co.aim.common.dto.powder.IdocH2TransResponseDto;
import kr.co.aim.common.dto.powder.IdocOrderMasterResponseDto;
import kr.co.aim.domain.model.GALDetailInterfaceResponse;
import kr.co.aim.domain.model.GALInterfaceResponse;
import kr.co.aim.domain.model.GALPartResponse;
import kr.co.aim.domain.repository.GALInterfaceRepository;
import kr.co.aim.infra.persistence.db2entity.powder.H2OrderDPEntity;
import kr.co.aim.infra.persistence.db2springdatajpa.powder.H2OrderDPJpaRepository;
import kr.co.aim.infra.persistence.db2springdatajpa.powder.H2OrderMPJpaRepository;
import kr.co.aim.infra.persistence.db2springdatajpa.powder.H2TransPJpaRepository;
import kr.co.aim.infra.persistence.db2springdatajpa.powder.IdocPJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile({"scheduler","web","simulator"})
@ConditionalOnProperty(name = "factory.type", havingValue = "powder")
public class PowderExternalInterfaceService implements FactoryGALInterfaceStrategy {

    private final IdocPJpaRepository idocPJpaRepository;
    private final H2OrderMPJpaRepository h2OrderMPJpaRepository;
    private final H2OrderDPJpaRepository h2OrderDPJpaRepository;
    private final H2TransPJpaRepository h2TransPJpaRepository;
    private final GALInterfaceRepository galInterfaceRepository;

    @Override
    @Transactional(value = "db2TransactionManager")
    public Page<GALInterfaceResponse> getInterfaceList(GALInterfaceSearchCondition condition, Pageable pageable) {
        return galInterfaceRepository.getInterfaceList(condition,pageable);
    }

    @Override
    @Transactional(value = "db2TransactionManager")
    public Page<GALDetailInterfaceResponse> getDetailInterfaceList(GALDetailInterfaceSearchCondition condition, Pageable pageable) {
        return galInterfaceRepository.getDetailInterfaceList(condition,pageable);
    }

    @Override
    @Transactional(value = "db2TransactionManager")
    public Page<GALPartResponse> getPartList(GALPartSearchCondition condition, Pageable pageable) {
        return galInterfaceRepository.getPartList(condition,pageable);
    }

    @Transactional(value = "db2TransactionManager")
    public Page<IdocOrderMasterResponseDto> findIdocWithOrderMasterByIdocTypId(Long idocTypId, Pageable pageable) {
        return idocPJpaRepository.findIdocWithOrderMasterByIdocTypId(idocTypId,pageable);
    }

    @Transactional(value = "db2TransactionManager")
    public Page<IdocH2TransResponseDto> findIdocWithH2TransByGalKey(String galKey, Pageable pageable) {
        return idocPJpaRepository.findIdocWithH2TransByGalKey(galKey,pageable);
    }

    @Transactional(value = "db2TransactionManager")
    public Page<H2OrderDPEntity> findByIdocId(Long idocId, Pageable pageable) {
        return h2OrderDPJpaRepository.findByIdocId(idocId,pageable);
    }

    @Transactional(value = "db2TransactionManager")
    public Page<IdocH2PartMResponseDto> findIdocWithPartMasterByIdocId(
            Long idocId,
            Pageable pageable
    ){
        return idocPJpaRepository.findIdocWithPartMasterByIdocId(idocId,pageable);
    }
}