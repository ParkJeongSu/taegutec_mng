package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.PasswordPolicy;
import kr.co.aim.infra.persistence.entity.PasswordPolicyEntity;
import kr.co.aim.infra.persistence.entity.PasswordPolicyHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        imports = { kr.co.aim.common.Utils.TsidUtils.class }
)
public interface PasswordPolicyMapper {

    PasswordPolicy toDomain(PasswordPolicyEntity entity);

    PasswordPolicyEntity toEntity(PasswordPolicy domain);

    @Mapping(target = "id", expression = "java(TsidUtils.nextId())")
    PasswordPolicyHistoryEntity toHistoryEntity(PasswordPolicy domain);
}
