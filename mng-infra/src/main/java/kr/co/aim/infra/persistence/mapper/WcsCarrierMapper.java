package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.WcsCarrier;
import kr.co.aim.infra.persistence.entity.WcsCarrierEntity;
import kr.co.aim.infra.persistence.entity.WcsCarrierHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface WcsCarrierMapper {

    WcsCarrier toDomain(WcsCarrierEntity entity);

    WcsCarrierEntity toEntity(WcsCarrier domain);

    @Mapping(target = "eventTimeKey", expression = "java(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern(\"yyyyMMddHHmmssSSS\")))")
    @Mapping(target = "eventName", source = "lastEventName")
    @Mapping(target = "eventTime", source = "lastEventTime")
    @Mapping(target = "eventUser", source = "lastEventUser")
    @Mapping(target = "eventComment", source = "lastEventComment")
    @Mapping(target = "travelProfile", expression = "java(parseTravelProfile(domain.getTravelProfile()))")
    WcsCarrierHistoryEntity toHistoryEntity(WcsCarrier domain);

    default int parseTravelProfile(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
