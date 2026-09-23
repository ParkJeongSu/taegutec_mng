package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.WcsZone;
import kr.co.aim.infra.persistence.entity.WcsZoneEntity;
import kr.co.aim.infra.persistence.entity.WcsZoneHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface WcsZoneMapper {

    WcsZone toDomain(WcsZoneEntity entity);

    WcsZoneEntity toEntity(WcsZone domain);

    @Mapping(target = "eventTimeKey", expression = "java(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern(\"yyyyMMddHHmmssSSS\")))")
    @Mapping(target = "eventName", source = "lastEventName")
    @Mapping(target = "eventTime", source = "lastEventTime")
    @Mapping(target = "eventUser", source = "lastEventUser")
    @Mapping(target = "eventComment", source = "lastEventComment")
    WcsZoneHistoryEntity toHistoryEntity(WcsZone domain);
}
