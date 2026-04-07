package kr.co.aim.domain.model;

import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class PortDef {
    private PortDefId id;
    private String description;
    private String portType;
    private String detailPortType;
    private String portUseType;
    private String workCenterName;
    private String locationId;
    private String checkOutState;
    private LocalDateTime checkOutTime;
    private String checkOutUser;
    private String dataState;
    private String lastEventName;
    private LocalDateTime lastEventTime;
    private String lastEventUser;
    private String lastEventComment;
}
