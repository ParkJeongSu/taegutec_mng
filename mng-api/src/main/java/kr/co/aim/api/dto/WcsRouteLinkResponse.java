package kr.co.aim.api.dto;

import kr.co.aim.domain.model.WcsRouteLink;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsRouteLinkResponse {

    private String factoryName;
    private Long routeLinkId;
    private String description;
    private String fromNodeId;
    private Integer length;
    private String passYn;
    private Integer priority;
    private String processType;
    private String routeLinkType;
    private String toNodeId;
    private String usableYn;
    private String useYn;

    private String lastEventComment;
    private String lastEventName;
    private LocalDateTime lastEventTime;
    private String lastEventUser;

    public static WcsRouteLinkResponse fromDomain(WcsRouteLink domain) {
        if (domain == null) {
            return null;
        }

        return WcsRouteLinkResponse.builder()
                .factoryName(domain.getFactoryName())
                .routeLinkId(domain.getRouteLinkId())
                .description(domain.getDescription())
                .fromNodeId(domain.getFromNodeId())
                .length(domain.getLength())
                .passYn(domain.getPassYn())
                .priority(domain.getPriority())
                .processType(domain.getProcessType())
                .routeLinkType(domain.getRouteLinkType())
                .toNodeId(domain.getToNodeId())
                .usableYn(domain.getUsableYn())
                .useYn(domain.getUseYn())
                .lastEventComment(domain.getLastEventComment())
                .lastEventName(domain.getLastEventName())
                .lastEventTime(domain.getLastEventTime())
                .lastEventUser(domain.getLastEventUser())
                .build();
    }
}
