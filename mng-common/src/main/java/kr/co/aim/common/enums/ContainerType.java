package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ContainerType implements MetaDataEnum {
    NONE("NO"),
    BTO_YTO("TO"),
    DOPING("DO"),
    CARBON("CB"),
    CARBON_WEIGHING("CW"),
    WPR_WP("WP"),
    WCM_WC("WC");
    private final String value;
}
