package kr.co.aim.common.format;

import lombok.*;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessData {
    private String siteName;
    private String siteType;
    private String siteValue;

}