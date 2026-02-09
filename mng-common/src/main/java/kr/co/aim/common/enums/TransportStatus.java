package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TransportStatus implements MetaDataEnum {
    Create("Create"),
    Accept("2"),
    Released("6"),
    InternalRelocation("114"),
    OutOfRack("109"),
    BinEmpty("103"),
    Shortage("82"),
    NotAllowedPickUp("115"),
    ArrivedAtWorkStation("108"),
    OrderDone("90"),
    TakeOff("112");
    private final String value;

    // "Name : Value" 형태로 반환하는 메소드
    public String getFullStatus() {
        return this.name() + " : " + this.value;
    }
}
