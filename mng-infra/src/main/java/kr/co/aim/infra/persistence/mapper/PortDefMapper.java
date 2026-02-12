package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.PortDef;
import kr.co.aim.infra.persistence.entity.PortDefEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
        // builder = @Builder(disableBuilder = true)  <- 이 라인을 삭제
)
public interface PortDefMapper {

    PortDef toDomain(PortDefEntity entity);

    PortDefEntity toEntity(PortDef domain);
}