package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum IF_EVENT_LOGEventType implements MetaDataEnum {
    MATERIAL_DEASSIGN_FROM_CARRIER("MaterialDeassignFromCarrier"),
    JOB_START("JobStart"),
    JOB_END("JobEnd"),
    MATERIAL_ASSIGN_TO_CARRIER("MaterialAssignToCarrier");
    private final String value;
}
