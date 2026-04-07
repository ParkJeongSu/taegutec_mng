package kr.co.aim.common.format;

//import ezieco.eziframe.middleware.event.EziMessage;
import lombok.*;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoadCompletedBody {
    private String equipmentName;
    private String portName;
    private String carrierName;
    private String portType;
    private String portTransportMode;
    private String transportJobName;
    private String actualWeight;
}
