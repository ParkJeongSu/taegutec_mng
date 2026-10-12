package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.InsertAutoTestResult;
import kr.co.aim.infra.persistence.entity.InsertAutoTestResultEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        imports = { kr.co.aim.common.Utils.TsidUtils.class }
)
public interface InsertAutoTestResultMapper {

    InsertAutoTestResult toDomain(InsertAutoTestResultEntity entity);

    InsertAutoTestResultEntity toEntity(InsertAutoTestResult domain);
}
