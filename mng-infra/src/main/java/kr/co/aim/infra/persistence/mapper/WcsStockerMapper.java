package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.WcsStocker;
import kr.co.aim.infra.persistence.entity.WcsStockerEntity;
import kr.co.aim.infra.persistence.entity.WcsStockerHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface WcsStockerMapper {

    WcsStocker toDomain(WcsStockerEntity entity);

    WcsStockerEntity toEntity(WcsStocker domain);

    @Mapping(target = "eventTimeKey", expression = "java(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern(\"yyyyMMddHHmmssSSS\")))")
    @Mapping(target = "eventName", source = "lastEventName")
    @Mapping(target = "eventTime", source = "lastEventTime")
    @Mapping(target = "eventUser", source = "lastEventUser")
    @Mapping(target = "eventComment", source = "lastEventComment")
    WcsStockerHistoryEntity toHistoryEntity(WcsStocker domain);
}
