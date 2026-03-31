package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.InterfaceEventLog;
import kr.co.aim.infra.persistence.entity.InterfaceEventLogEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        imports = { kr.co.aim.common.Utils.TsidUtils.class} // [핵심] 이 부분을 추가하세요!
)
public interface InterfaceEventLogMapper {

    InterfaceEventLog toDomain(InterfaceEventLogEntity entity);

    InterfaceEventLogEntity toEntity(InterfaceEventLog domain);
}