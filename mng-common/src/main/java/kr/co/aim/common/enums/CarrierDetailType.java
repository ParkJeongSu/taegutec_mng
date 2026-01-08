package kr.co.aim.common.enums;

import kr.co.aim.common.handler.DependentMetaDataEnum;
import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CarrierDetailType implements DependentMetaDataEnum {

    // CarrierType.CONTAINER 자식
    TO("TO", CarrierType.CONTAINER),
    DO("DO", CarrierType.CONTAINER),
    SAMPLE("Sample",CarrierType.CST),
    DUMMY("Dummy",CarrierType.CST),
    SCRAP("Scrap",CarrierType.CST);
    private final String value;
    private final CarrierType carrierType;

    @Override
    public MetaDataEnum getParent() {
        return this.carrierType; // 2. 필드로 가진 부모를 반환
    }
}
