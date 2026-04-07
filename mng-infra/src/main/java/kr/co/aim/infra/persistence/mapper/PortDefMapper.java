package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.PortDef;
import kr.co.aim.infra.persistence.entity.PortDefEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface PortDefMapper {

    PortDef toDomain(PortDefEntity entity);

    PortDefEntity toEntity(PortDef domain);
}