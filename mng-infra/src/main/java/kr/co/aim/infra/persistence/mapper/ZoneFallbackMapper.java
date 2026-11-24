package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.ZoneFallback;
import kr.co.aim.infra.persistence.entity.ZoneFallbackEntity;
import kr.co.aim.infra.persistence.entity.ZoneFallbackHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        imports = { kr.co.aim.common.Utils.TsidUtils.class }
)
public interface ZoneFallbackMapper {

    ZoneFallback toDomain(ZoneFallbackEntity entity);

    ZoneFallbackEntity toEntity(ZoneFallback domain);

    @Mapping(target = "id", expression = "java(TsidUtils.nextId())")
    ZoneFallbackHistoryEntity toHistoryEntity(ZoneFallback domain);

    @Mapping(target = "id", expression = "java(TsidUtils.nextId())")
    ZoneFallbackHistoryEntity toHistoryEntity(ZoneFallbackEntity entity);
}
