package kr.co.aim.infra.persistence.entitydb2;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Table(name = "IF_WORK_ORDER")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA를 위한 기본 생성자
public class IF_WorkOrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String workOrderName;
    private String description;
    private String vendorId;
    private String productDefId;
    @Column(name="PROCESSFLOW_ID")
    private String processFlowId;
    @Column(name="PROCESSOPERATION_ID")
    private String processOperationId;
    private String equipmentName;
    private Integer planQuantity;
    private String ifState;
    private LocalDateTime createTime;
    private LocalDateTime dueDate;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;

}
