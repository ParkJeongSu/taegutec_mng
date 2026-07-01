package kr.co.aim.infra.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.co.aim.common.handler.IBaseHistoryEntity;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@Table(name = "EQUIPMENT_HISTORY", catalog = "NEXBEMNG", schema = "dbo")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA를 위한 기본 생성자
@Builder
public class EquipmentHistoryEntity implements IBaseHistoryEntity {
    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "EQUIPMENT_NAME")
    private String equipmentName;

    @Column(name = "PARENT_EQUIPMENT_ID")
    private Long parentEquipmentId;

    @Column(name = "EQUIPMENT_LEVEL")
    private String equipmentLevel;

    @Column(name = "EQUIPMENT_STATE")
    private String equipmentState;

    @Column(name = "COMMUNICATION_STATE")
    private String communicationState;

    @Column(name = "LOADING_COUNT")
    private Integer loadingCount;

    @Column(name = "PROCESS_COUNT")
    private Integer processCount;

    @Column(name = "RECIPE_NAME")
    private String recipeName;

    @Column(name = "HOLD_STATE")
    private String holdState;

    @Column(name = "REASON_CODE")
    private String reasonCode;

    @Column(name = "RESOURCE_STATE")
    private String resourceState;

    @Column(name = "OPERATION_MODE")
    private String operationMode;

    @Column(name = "MESSAGE_SERVICE_ADDRESS")
    private String messageServiceAddress;

    @Column(name = "EVENT_NAME")
    private String eventName;

    @Column(name = "EVENT_TIME")
    private LocalDateTime eventTime;

    @Column(name = "EVENT_USER")
    private String eventUser;

    @Column(name = "EVENT_COMMENT")
    private String eventComment;

    @Column(name = "PRODUCTION_ORDER_ID")
    private Long productionOrderId;
}
