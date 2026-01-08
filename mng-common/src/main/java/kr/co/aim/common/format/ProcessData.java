package kr.co.aim.common.format;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class ProcessData {
    private String siteName;
    private String siteType;
    private String siteValue;

}