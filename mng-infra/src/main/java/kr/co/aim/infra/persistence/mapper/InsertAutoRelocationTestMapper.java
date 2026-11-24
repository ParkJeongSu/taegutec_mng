package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.InsertAutoRelocationTest;
import kr.co.aim.infra.persistence.entity.InsertAutoRelocationTestEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        imports = { kr.co.aim.common.Utils.TsidUtils.class }
)
public interface InsertAutoRelocationTestMapper {

    InsertAutoRelocationTest toDomain(InsertAutoRelocationTestEntity entity);

    InsertAutoRelocationTestEntity toEntity(InsertAutoRelocationTest domain);
}
