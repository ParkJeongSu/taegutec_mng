package kr.co.aim.api.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsSubTransferRuleUpdateRequestDto {

    private Integer carrierCount;
    private String description;
    private String moduleType;
    private String ngStatus;

    private String eventName;
    private String eventUser;
    private String eventComment;
}
