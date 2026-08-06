package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GALProductionStatus implements MetaDataEnum {
    ERROR("0"),
    ACCEPT("2"),
    RELEASE("6"),
    FIBC_ON_PALLET("10"),
    PALLET_LOAD_COMPLETED_TO_WAREHOUSE("13"),
    STOCK_CHANGED("51"),
    CHANGED_STOCK_PER_CONTAINER("13"),
    UNPACK_STARTED("14"),
    UNPACK_ENDED("15"),
    UNPACK_ENDED_STOCK_TO_WIP("21"),
    PRODUCTION_STARTED("22"),
    PRODUCTION_ENDED("23"),
    PRODUCTION_ENDED_STOCK_TO_WIP("31"),
    PACKING_STARTED("32"),
    PACKING_ENDED("33"),
    WHAT_IS_NEXT_RRN("17"),
    MOVE_RRN("50"),
    MISSING_QUANTITY("70"),
    SURPLUS_QUANTITY("72"),
    MOVE_RRN_COMPLETED("95"),
    ORDER_LINE_NO_COMPLETED("93"),
    ORDER_COMPLETED("92"),
    SHORTAGE("82"),
    REASSIGN_RRN("96"),
    CREATED_PART_MASTER("4"),
    CHANGED_PART_MASTER("230"),
    CYCLE_COUNT_DATA("78"),
    CYCLE_COUNT_MINUS_DIFFERENCE("74"),
    CYCLE_COUNT_PLUS_DIFFERENCE("76");
    private final String value;

    // "Name : Value" 형태로 반환하는 메소드
    public String getFullStatus() {
        return this.name() + " : " + this.value;
    }
}
