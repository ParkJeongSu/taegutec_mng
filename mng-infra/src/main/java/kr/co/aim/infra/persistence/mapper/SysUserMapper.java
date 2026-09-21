package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.SysUser;
import kr.co.aim.infra.persistence.entity.SysUserEntity;
import kr.co.aim.infra.persistence.entity.SysUserHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        imports = { kr.co.aim.common.Utils.TsidUtils.class }
)
public interface SysUserMapper {

    SysUser toDomain(SysUserEntity entity);

    SysUserEntity toEntity(SysUser domain);

    @Mapping(target = "id", expression = "java(TsidUtils.nextId())")
    SysUserHistoryEntity toHistoryEntity(SysUser domain);
}
