package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.EquipmentDef;
import kr.co.aim.infra.persistence.entity.EquipmentDefEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
        // builder = @Builder(disableBuilder = true)  <- 이 라인을 삭제
)
public interface EquipmentDefMapper {

    EquipmentDef toDomain(EquipmentDefEntity entity);

    EquipmentDefEntity toEntity(EquipmentDef domain);
}