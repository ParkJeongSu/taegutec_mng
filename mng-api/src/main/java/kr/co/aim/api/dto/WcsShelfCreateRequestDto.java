package kr.co.aim.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsShelfCreateRequestDto {

    @NotBlank(message = "공장 구분은 필수 입력 항목입니다.")
    private String factoryName;

    @NotBlank(message = "스토커 명은 필수 입력 항목입니다.")
    private String stockerName;

    @NotBlank(message = "셸프 명은 필수 입력 항목입니다.")
    private String shelfName;

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
