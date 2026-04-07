package kr.co.aim.common.format;

import lombok.*;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortUseTypeChangedBody {
    private String equipmentName;
    private String portName;
    private String portUseType;
    private String portType;
    private String portTransportMode;
}
