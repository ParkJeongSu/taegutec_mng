package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import kr.co.aim.common.handler.ParentMetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
public enum CarrierType implements ParentMetaDataEnum {
    CST("CST"),
    TRAY("Tray"),
    MAGAZINE("Magazine"),
    CONTAINER("Container"),
    PALLET("Pallet");
    private final String value;

    @Override
    public List<MetaDataEnum> getChildList() {
        List<MetaDataEnum> result = new ArrayList<>();

        // CarrierDetailType의 모든 값을 순회합니다.
        for (CarrierDetailType detailType : CarrierDetailType.values()) {

            // 상세 타입의 부모(carrierType)가 '나'(this)와 일치하는지 확인
            if (detailType.getCarrierType() == this) {
                result.add(detailType);
            }
        }
        return result;
    }
}
