package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GALProductionStatus implements MetaDataEnum {
    Create("Create"),
    Accept("2"),
    Released("6"),
    FibcOnPallet("10"),
    PalletLoadCompletedToWarehouse("13"),
    ProductionStarted("14"),
    ProductionEnded("15"),
    MissingQuantity("70"),
    SurplusQuantity("72"),
    MoveRRNCompleted("95"),
    OrderLineNoCompleted("93"),
    OrderCompleted("92"),
    Shortage("82"),
    Reassign_RRN("96"),
    CreatedPartMaster("4"),
    ChangedPartMaster("230"),
    CycleCountData("78"),
    CycleCountMinusDifference("74"),
    CycleCountPlusDifference("76");
    private final String value;

    // "Name : Value" 형태로 반환하는 메소드
    public String getFullStatus() {
        return this.name() + " : " + this.value;
    }
}
