package kr.co.aim.common.format;

import lombok.*;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortStateChangedBody {
    private String equipmentName;
    private String portName;
    private String portType;
    private String portStateName;
}
