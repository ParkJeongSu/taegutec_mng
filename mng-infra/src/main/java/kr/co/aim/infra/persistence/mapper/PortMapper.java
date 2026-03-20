package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.Port;
import kr.co.aim.infra.persistence.entity.PortEntity;
import kr.co.aim.infra.persistence.entity.PortHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        imports = { kr.co.aim.common.Utils.TsidUtils.class} // [핵심] 이 부분을 추가하세요!
)
public interface PortMapper {

    //@BeanMapping(ignoreByDefault = true) // 👈 이 한 줄 추가
    //BeanMapping 이라는건 기본적으로 모든 필드를 무시하고 @Mapping 으로 명시한 컬럼만 매핑
    Port toDomain(PortEntity entity);

    PortEntity toEntity(Port domain);

    @Mapping(target = "id", expression = "java(TsidUtils.nextId())") // [3] 자바 코드 호출!
    PortHistoryEntity toHistoryEntity(Port domain);
}