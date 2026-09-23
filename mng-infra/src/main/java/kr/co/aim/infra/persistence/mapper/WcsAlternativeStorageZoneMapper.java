package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.WcsAlternativeStorageZone;
import kr.co.aim.infra.persistence.entity.WcsAlternativeStorageZoneEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface WcsAlternativeStorageZoneMapper {

    WcsAlternativeStorageZone toDomain(WcsAlternativeStorageZoneEntity entity);

    WcsAlternativeStorageZoneEntity toEntity(WcsAlternativeStorageZone domain);
}
