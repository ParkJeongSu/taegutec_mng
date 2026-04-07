package kr.co.aim.common.format;

import lombok.*;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunicationStateChangedBody {
    private String equipmentName;
    private String equipmentType;
    private String communicationState;
}
