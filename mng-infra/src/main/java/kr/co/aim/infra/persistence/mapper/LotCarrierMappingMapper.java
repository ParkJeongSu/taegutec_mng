package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.LotCarrierMapping;
import kr.co.aim.domain.model.LotCarrierMappingHistory;
import kr.co.aim.infra.persistence.entity.LotCarrierMappingEntity;
import kr.co.aim.infra.persistence.entity.LotCarrierMappingHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        imports = { kr.co.aim.common.Utils.TsidUtils.class }
)
public interface LotCarrierMappingMapper {

    LotCarrierMapping toDomain(LotCarrierMappingEntity entity);

    LotCarrierMappingEntity toEntity(LotCarrierMapping domain);

    LotCarrierMappingHistory toDomain(LotCarrierMappingHistoryEntity entity);

    LotCarrierMappingHistoryEntity toEntity(LotCarrierMappingHistory domain);

    @Mapping(target = "id", expression = "java(TsidUtils.nextId())")
    LotCarrierMappingHistoryEntity toHistoryEntity(LotCarrierMapping domain);
}