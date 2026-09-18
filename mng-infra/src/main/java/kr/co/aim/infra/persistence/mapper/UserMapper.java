package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.User;
import kr.co.aim.infra.persistence.entity.UserEntity;
import kr.co.aim.infra.persistence.entity.UserHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        imports = { kr.co.aim.common.Utils.TsidUtils.class }
)
public interface UserMapper {

    User toDomain(UserEntity entity);

    UserEntity toEntity(User domain);

    @Mapping(target = "id", expression = "java(TsidUtils.nextId())")
    UserHistoryEntity toHistoryEntity(User domain);
}
