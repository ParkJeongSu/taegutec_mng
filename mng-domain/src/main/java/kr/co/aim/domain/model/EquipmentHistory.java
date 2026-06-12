package kr.co.aim.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.co.aim.common.handler.IBaseHistoryEntity;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class EquipmentHistory implements IBaseHistoryEntity {
    private Long id;
    private String equipmentName;
    private Long parentEquipmentId;
    private String equipmentLevel;
    private String equipmentState;
    private String communicationState;
    private Integer loadingCount;
    private Integer processCount;
    private String recipeName;
    private String holdState;
    private String reasonCode;
    private String resourceState;
    private String operationMode;
    private String messageServiceAddress;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;
    private Long productionOrderId;
}
