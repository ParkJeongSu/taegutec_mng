package kr.co.aim.api.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsRouteLinkUpdateRequestDto {

    private String description;
    private Long fromNodeId;
    private Integer length;
    private String passYn;
    private Integer priority;
    private String processType;
    private String routeLinkType;
    private Long toNodeId;
    private String usableYn;
    private String useYn;

    private String eventName;
    private String eventUser;
    private String eventComment;
}
