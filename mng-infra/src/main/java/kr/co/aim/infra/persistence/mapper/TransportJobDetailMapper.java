package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.TransportJobDetail;
import kr.co.aim.infra.persistence.entity.TransportJobDetailEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface TransportJobDetailMapper {

    TransportJobDetail toDomain(TransportJobDetailEntity entity);

    TransportJobDetailEntity toEntity(TransportJobDetail domain);
}