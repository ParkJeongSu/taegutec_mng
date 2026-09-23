package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.WcsRouteNode;
import kr.co.aim.infra.persistence.entity.WcsRouteNodeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface WcsRouteNodeMapper {

    WcsRouteNode toDomain(WcsRouteNodeEntity entity);

    WcsRouteNodeEntity toEntity(WcsRouteNode domain);
}
