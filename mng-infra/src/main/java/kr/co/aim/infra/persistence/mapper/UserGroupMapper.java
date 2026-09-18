package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.UserGroup;
import kr.co.aim.infra.persistence.entity.UserGroupEntity;
import kr.co.aim.infra.persistence.entity.UserGroupHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        imports = { kr.co.aim.common.Utils.TsidUtils.class }
)
public interface UserGroupMapper {

    UserGroup toDomain(UserGroupEntity entity);

    UserGroupEntity toEntity(UserGroup domain);

    @Mapping(target = "id", expression = "java(TsidUtils.nextId())")
    UserGroupHistoryEntity toHistoryEntity(UserGroup domain);
}
