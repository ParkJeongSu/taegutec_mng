package kr.co.aim.common.format;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class ProcessJobStartedBody {
    private String equipmentName;
    private String portName;
    private String lotName;
    private String carrierName;
    private String recipeName;
}
