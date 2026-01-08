package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.common.dto.UserResponseDto;
import kr.co.aim.domain.model.User;
import kr.co.aim.infra.persistence.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toDomain(UserEntity entity);

    UserEntity toEntity(User domain);

    @Mapping(target = "authorityName", ignore = true) // "id" 필드를 매핑 대상에서 제외
    UserResponseDto toResponseDto(User domain);
}