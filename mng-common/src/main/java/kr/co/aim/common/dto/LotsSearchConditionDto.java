package kr.co.aim.common.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED) // JPA Entity 등을 위한 기본 생성자
public class LotsSearchConditionDto {

    private Long id;
    private String lotName;
    private String productionType;
    private String lotState;
    private String processState;
    private String productDefId;
    private String processSpecId;
    private String processSpecVersion;
    private String processFlowId;
    private String processOperationId;
    private String workOrderId;
    private String equipmentName;
    private String portName;
    private Long carrierId;
    private String lotGrade;
    private String holdState;

}