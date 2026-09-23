package kr.co.aim.api.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsAlternativeStorageZoneUpdateRequestDto {

    private String description;
    private String useYn;

    private String eventName;
    private String eventUser;
    private String eventComment;
}
