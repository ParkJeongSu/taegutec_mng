package kr.co.aim.common.format;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class CommunicationStateChangedBody {
    private String equipmentName;
    private String equipmentType;
    private String communicationState;
}
