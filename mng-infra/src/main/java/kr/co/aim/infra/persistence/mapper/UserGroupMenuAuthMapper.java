package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.UserGroupMenuAuth;
import kr.co.aim.infra.persistence.entity.UserGroupMenuAuthEntity;
import kr.co.aim.infra.persistence.entity.UserGroupMenuAuthHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        imports = { kr.co.aim.common.Utils.TsidUtils.class }
)
public interface UserGroupMenuAuthMapper {

    UserGroupMenuAuth toDomain(UserGroupMenuAuthEntity entity);

    UserGroupMenuAuthEntity toEntity(UserGroupMenuAuth domain);

    @Mapping(target = "id", expression = "java(TsidUtils.nextId())")
    UserGroupMenuAuthHistoryEntity toHistoryEntity(UserGroupMenuAuth domain);
}
