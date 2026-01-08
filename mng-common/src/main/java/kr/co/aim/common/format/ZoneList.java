package kr.co.aim.common.format;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class ZoneList {
    private String zoneName;
    private String totalCapacity;
    private String usingShelfCount;
    private String emptyShelfCount;
}