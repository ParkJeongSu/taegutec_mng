package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.WcsPort;
import kr.co.aim.infra.persistence.entity.WcsPortEntity;
import kr.co.aim.infra.persistence.entity.WcsPortHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface WcsPortMapper {

    WcsPort toDomain(WcsPortEntity entity);

    WcsPortEntity toEntity(WcsPort domain);

    @Mapping(target = "eventTimeKey", expression = "java(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern(\"yyyyMMddHHmmssSSS\")))")
    @Mapping(target = "eventName", source = "lastEventName")
    @Mapping(target = "eventTime", source = "lastEventTime")
    @Mapping(target = "eventUser", source = "lastEventUser")
    @Mapping(target = "eventComment", source = "lastEventComment")
    WcsPortHistoryEntity toHistoryEntity(WcsPort domain);
}
