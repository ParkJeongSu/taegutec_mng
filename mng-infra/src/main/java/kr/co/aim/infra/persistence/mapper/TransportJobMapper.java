package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.TransportJob;
import kr.co.aim.infra.persistence.entity.TransportJobEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface TransportJobMapper {

    TransportJob toDomain(TransportJobEntity entity);

    TransportJobEntity toEntity(TransportJob domain);
}