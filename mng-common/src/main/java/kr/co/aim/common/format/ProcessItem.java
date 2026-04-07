package kr.co.aim.common.format;

import lombok.*;

import java.util.List;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessItem {
    private String itemName;
    private String dataType;
    private List<ProcessData> siteList;

}