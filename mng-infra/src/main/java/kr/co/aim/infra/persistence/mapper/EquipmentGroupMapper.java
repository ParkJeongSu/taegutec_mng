package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.common.dto.EquipmentGroupResponseDto;
import kr.co.aim.domain.model.EquipmentGroup;
import kr.co.aim.infra.persistence.entity.EquipmentGroupEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
        // builder = @Builder(disableBuilder = true)  <- 이 라인을 삭제
)
public interface EquipmentGroupMapper {

    EquipmentGroup toDomain(EquipmentGroupEntity entity);

    EquipmentGroupEntity toEntity(EquipmentGroup domain);

    EquipmentGroupResponseDto toResponseDto(EquipmentGroup domain);
}