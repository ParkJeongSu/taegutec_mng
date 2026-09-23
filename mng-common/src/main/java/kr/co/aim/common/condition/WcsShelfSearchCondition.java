package kr.co.aim.common.condition;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsShelfSearchCondition {

    private String factoryName;
    private String stockerName;
    private String shelfName;
    private String zoneName;
    private String shelfStatus;
    private String carrierName;
    private String shelfType;
    private String shelfEnableMode;
    private String shelfTransferStatus;
    private Integer row;
    private Integer col;
    private Integer stage;
    private Integer bin;
}
