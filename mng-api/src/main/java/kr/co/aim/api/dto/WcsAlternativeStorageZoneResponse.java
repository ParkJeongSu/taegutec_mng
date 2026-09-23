package kr.co.aim.api.dto;

import kr.co.aim.domain.model.WcsAlternativeStorageZone;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsAlternativeStorageZoneResponse {

    private String alternativeZoneName;
    private String factoryName;
    private Integer priority;
    private String sourceZoneName;
    private String description;
    private String useYn;

    private String lastEventComment;
    private String lastEventName;
    private LocalDateTime lastEventTime;
    private String lastEventUser;

    public static WcsAlternativeStorageZoneResponse fromDomain(WcsAlternativeStorageZone domain) {
        if (domain == null) {
            return null;
        }

        return WcsAlternativeStorageZoneResponse.builder()
                .alternativeZoneName(domain.getAlternativeZoneName())
                .factoryName(domain.getFactoryName())
                .priority(domain.getPriority())
                .sourceZoneName(domain.getSourceZoneName())
                .description(domain.getDescription())
                .useYn(domain.getUseYn())
                .lastEventComment(domain.getLastEventComment())
                .lastEventName(domain.getLastEventName())
                .lastEventTime(domain.getLastEventTime())
                .lastEventUser(domain.getLastEventUser())
                .build();
    }
}
