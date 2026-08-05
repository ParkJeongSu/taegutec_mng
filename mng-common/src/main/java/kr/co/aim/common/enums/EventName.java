package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EventName implements MetaDataEnum {
    CREATED("CREATED"),
    REMOVED("REMOVED"),
    TRANSFER("TRANSFER"),
    BIND_PALLET_BAG("BIND_PALLET_BAG"),
    RAW_MATERIAL_INBOUND_REQUEST("RAW_MATERIAL_INBOUND_REQUEST"),
    ALLOCATE_REQUEST("ALLOCATE_REQUEST"),
    AUTO_TRANSPORT("AUTO_TRANSPORT"),
    ALLOCATE("ALLOCATE"),
    RAW_MATERIAL_RECEIVING_START("RAW_MATERIAL_RECEIVING_START"),
    SAVE_INTERFACE_EVENT_LOG("SAVE_INTERFACE_EVENT_LOG"),
    UPDATED("UPDATED");

    private final String value;
}
