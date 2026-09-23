package kr.co.aim.api.dto;

import kr.co.aim.domain.model.WcsSubTransferRule;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsSubTransferRuleResponse {

    private String equipmentName;
    private String factoryName;
    private String moduleName;
    private Long routeLinkId;
    private Integer carrierCount;
    private String description;
    private String moduleType;
    private String ngStatus;

    private String lastEventComment;
    private String lastEventName;
    private LocalDateTime lastEventTime;
    private String lastEventUser;

    public static WcsSubTransferRuleResponse fromDomain(WcsSubTransferRule domain) {
        if (domain == null) {
            return null;
        }

        return WcsSubTransferRuleResponse.builder()
                .equipmentName(domain.getEquipmentName())
                .factoryName(domain.getFactoryName())
                .moduleName(domain.getModuleName())
                .routeLinkId(domain.getRouteLinkId())
                .carrierCount(domain.getCarrierCount())
                .description(domain.getDescription())
                .moduleType(domain.getModuleType())
                .ngStatus(domain.getNgStatus())
                .lastEventComment(domain.getLastEventComment())
                .lastEventName(domain.getLastEventName())
                .lastEventTime(domain.getLastEventTime())
                .lastEventUser(domain.getLastEventUser())
                .build();
    }
}
