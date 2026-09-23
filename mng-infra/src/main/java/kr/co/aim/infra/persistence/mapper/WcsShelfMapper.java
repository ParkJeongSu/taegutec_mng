package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.WcsShelf;
import kr.co.aim.infra.persistence.entity.WcsShelfEntity;
import kr.co.aim.infra.persistence.entity.WcsShelfHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface WcsShelfMapper {

    WcsShelf toDomain(WcsShelfEntity entity);

    WcsShelfEntity toEntity(WcsShelf domain);

    @Mapping(target = "eventTimeKey", expression = "java(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern(\"yyyyMMddHHmmssSSS\")))")
    @Mapping(target = "eventName", source = "lastEventName")
    @Mapping(target = "eventTime", source = "lastEventTime")
    @Mapping(target = "eventUser", source = "lastEventUser")
    @Mapping(target = "eventComment", source = "lastEventComment")
    WcsShelfHistoryEntity toHistoryEntity(WcsShelf domain);
}
