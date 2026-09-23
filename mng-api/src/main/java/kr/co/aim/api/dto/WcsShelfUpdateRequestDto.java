package kr.co.aim.api.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsShelfUpdateRequestDto {

    private Integer abnormalStageNumber;
    private Integer bin;
    private String carrierName;
    private Integer col;
    private String lastTransferCmdName;
    private Integer numberOfUses;
    private Integer row;
    private String shelfEnableMode;
    private String shelfStatus;
    private String shelfTransferStatus;
    private String shelfType;
    private Integer stage;
    private String zoneName;

    private String eventName;
    private String eventUser;
    private String eventComment;
}
