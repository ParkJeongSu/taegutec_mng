package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GALTransportStatus implements MetaDataEnum {
    CREATED("CREATED"),
    ACCEPT("2"),
    RELEASED("6"),
    INTERNAL_RELOCATION("114"),
    OUT_OF_RACK("109"),
    STATION_OCCUPIED("106"),
    BIN_EMPTY("113"),
    SHORTAGE_OUTBOUND("82"),
    SHORTAGE_RELOCATION("86"),
    NOT_ALLOWED_PICK_UP("115"),
    ARRIVED_AT_WORK_STATION("108"),
    WORKSTATION_EMPTY("105"),
    ARRIVED_AT_RACK("107"),
    ORDER_DONE_OUTBOUND("90"),
    ORDER_DONE_INBOUND("92"),
    ORDER_DONE_RELOCATION("94"),
    CARRIER_SCANNED("126"),
    ARRIVED_AT_WORKSTATION_WITH_ERROR("110"),
    ERROR_TEXT("111"),
    DROPPED_ON_TUNNEL_CONVEYOR("109"),
    TAKE_OFF("112");
    private final String value;

    // "Name : Value" 형태로 반환하는 메소드
    public String getFullStatus() {
        return this.name() + " : " + this.value;
    }
}
