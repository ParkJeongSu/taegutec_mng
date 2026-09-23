package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.WcsRouteLink;
import kr.co.aim.infra.persistence.entity.WcsRouteLinkEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface WcsRouteLinkMapper {

    WcsRouteLink toDomain(WcsRouteLinkEntity entity);

    WcsRouteLinkEntity toEntity(WcsRouteLink domain);
}
