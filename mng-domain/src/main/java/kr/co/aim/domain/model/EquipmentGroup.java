package kr.co.aim.domain.model;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED) // JPA Entity 등을 위한 기본 생성자
@AllArgsConstructor
@ToString
@Builder
public class EquipmentGroup {
    private String equipmentGroupName;
    private String description;
    private String checkOutState;
    private LocalDateTime checkOutTime;
    private String checkOutUser;
    private String dataState;
    private String lastEventName;
    private LocalDateTime lastEventTime;
    private String lastEventUser;
    private String lastEventComment;
}
