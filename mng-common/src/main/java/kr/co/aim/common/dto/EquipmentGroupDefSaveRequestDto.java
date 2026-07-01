package kr.co.aim.common.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class EquipmentGroupDefSaveRequestDto {
    private Long id;
    private String equipmentGroupName;
    private String description;
    private String checkOutState;
    private LocalDateTime checkOutTime;
    private String checkOutUser;
    private String dataState;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;
}
