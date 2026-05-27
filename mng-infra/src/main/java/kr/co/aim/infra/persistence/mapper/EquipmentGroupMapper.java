package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.EquipmentGroup;
import kr.co.aim.infra.persistence.entity.EquipmentGroupDefEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        imports = { kr.co.aim.common.Utils.TsidUtils.class} // [핵심] 이 부분을 추가하세요!
)
public interface EquipmentGroupMapper {

    EquipmentGroup toDomain(EquipmentGroupDefEntity entity);

    EquipmentGroupDefEntity toEntity(EquipmentGroup domain);

}