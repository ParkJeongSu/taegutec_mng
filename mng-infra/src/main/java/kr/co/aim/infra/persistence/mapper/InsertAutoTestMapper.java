package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.InsertAutoTest;
import kr.co.aim.infra.persistence.entity.InsertAutoTestEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        imports = { kr.co.aim.common.Utils.TsidUtils.class }
)
public interface InsertAutoTestMapper {

    InsertAutoTest toDomain(InsertAutoTestEntity entity);

    InsertAutoTestEntity toEntity(InsertAutoTest domain);
}
