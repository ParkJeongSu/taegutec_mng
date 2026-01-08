package kr.co.aim.common.format;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class OperationModeReportBody {
    private String equipmentName;
    private String equipmentType;
    private String operationModeName;
    private String operationModeDescription;
}
