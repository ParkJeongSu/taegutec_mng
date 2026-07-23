package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.EquipmentGroupDef;
import kr.co.aim.domain.model.Port;
import kr.co.aim.infra.persistence.entity.EquipmentGroupDefEntity;
import kr.co.aim.infra.persistence.entity.EquipmentGroupDefHistoryEntity;
import kr.co.aim.infra.persistence.entity.PortHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        imports = { kr.co.aim.common.Utils.TsidUtils.class} // [핵심] 이 부분을 추가하세요!
)
public interface EquipmentGroupDefMapper {

    EquipmentGroupDef toDomain(EquipmentGroupDefEntity entity);

    EquipmentGroupDefEntity toEntity(EquipmentGroupDef domain);

    @Mapping(target = "id", expression = "java(TsidUtils.nextId())") // [3] 자바 코드 호출!
    EquipmentGroupDefHistoryEntity toHistoryEntity(EquipmentGroupDef domain);

}