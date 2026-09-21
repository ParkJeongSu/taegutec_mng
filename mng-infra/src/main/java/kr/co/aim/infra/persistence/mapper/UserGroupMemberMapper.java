package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.UserGroupMember;
import kr.co.aim.infra.persistence.entity.UserGroupMemberEntity;
import kr.co.aim.infra.persistence.entity.UserGroupMemberHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        imports = { kr.co.aim.common.Utils.TsidUtils.class }
)
public interface UserGroupMemberMapper {

    @Mapping(target = "employeeId", ignore = true)
    @Mapping(target = "userName", ignore = true)
    @Mapping(target = "userGroupName", ignore = true)
    @Mapping(target = "groupDescription", ignore = true)
    UserGroupMember toDomain(UserGroupMemberEntity entity);

    UserGroupMemberEntity toEntity(UserGroupMember domain);

    @Mapping(target = "id", expression = "java(TsidUtils.nextId())")
    UserGroupMemberHistoryEntity toHistoryEntity(UserGroupMember domain);
}
