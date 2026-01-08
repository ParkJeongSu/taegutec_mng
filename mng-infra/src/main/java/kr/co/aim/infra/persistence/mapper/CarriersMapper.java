package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.Carriers;
import kr.co.aim.infra.persistence.entity.CarriersEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CarriersMapper {

    Carriers toDomain(CarriersEntity entity);

    CarriersEntity toEntity(Carriers domain);
}