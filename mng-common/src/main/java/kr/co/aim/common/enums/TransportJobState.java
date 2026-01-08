package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TransportJobState implements MetaDataEnum {
    REQUESTED("Requested"),
    ACCEPTED("Accepted"),
    STARTED("Started"),
    REJECTED("Rejected"),
    COMPLETED("Completed"),
    TERMINATED("Terminated");
    private final String value;
}
