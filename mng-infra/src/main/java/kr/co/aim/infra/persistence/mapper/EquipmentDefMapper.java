package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.EquipmentDef;
import kr.co.aim.infra.persistence.entity.EquipmentDefEntity;
import kr.co.aim.infra.persistence.entity.EquipmentDefHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        imports = { kr.co.aim.common.Utils.TsidUtils.class} // [핵심] 이 부분을 추가하세요!
)
public interface EquipmentDefMapper {

    EquipmentDef toDomain(EquipmentDefEntity entity);

    EquipmentDefEntity toEntity(EquipmentDef domain);

    @Mapping(target = "id", expression = "java(TsidUtils.nextId())") // [3] 자바 코드 호출!
    EquipmentDefHistoryEntity toHistoryEntity(EquipmentDef domain);
}