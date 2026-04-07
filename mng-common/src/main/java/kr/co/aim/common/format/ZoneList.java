package kr.co.aim.common.format;

import lombok.*;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZoneList {
    private String zoneName;
    private String totalCapacity;
    private String usingShelfCount;
    private String emptyShelfCount;
}