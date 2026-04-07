package kr.co.aim.common.format;

import lombok.*;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationModeReportBody {
    private String equipmentName;
    private String equipmentType;
    private String operationModeName;
    private String operationModeDescription;
}
