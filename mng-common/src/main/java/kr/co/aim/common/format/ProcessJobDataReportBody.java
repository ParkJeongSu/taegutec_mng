package kr.co.aim.common.format;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@Builder
public class ProcessJobDataReportBody {
    private String equipmentName;
    private String lotName;
    private String carrierName;
    private String recipeName;
    private String processOperationName;
    private String productSpecName;

    private List<ProcessItem> itemList;
}
