package kr.co.aim.common.enums;

import java.util.*;

public enum GALTransportStatusRule {
    ;

    // key: 현재 처리할 코드, value: 선행되어야 하는 코드 목록 (하나라도 만족하면 OK)
    private static final Map<Long, List<Long>> PRECONDITION_RULES = new HashMap<>();

    static {
        // 111(ERROR_TEXT)은 110이 선행되어야 함
        PRECONDITION_RULES.put(
                Long.parseLong(GALTransportStatus.ERROR_TEXT.getValue()), // 111
                Collections.singletonList(Long.parseLong(GALTransportStatus.ARRIVED_AT_WORKSTATION_WITH_ERROR.getValue())) // 110
        );

        // OUTBOUND 순서 유지
        // 90(ORDER_DONE_OUTBOUND)은 108(도착) 또는 82(출고부족) 중 하나가 선행되어야 함
        PRECONDITION_RULES.put(
                Long.parseLong(GALTransportStatus.ORDER_DONE_OUTBOUND.getValue()), // 90
                Arrays.asList(
                        Long.parseLong(GALTransportStatus.ARRIVED_AT_WORK_STATION.getValue()), // 108
                        Long.parseLong(GALTransportStatus.SHORTAGE_OUTBOUND.getValue())         // 82
                )
        );
        // INBOUND 순서 유지
        // 92 는 107이 선행되어야함
        PRECONDITION_RULES.put(
                Long.parseLong(GALTransportStatus.ORDER_DONE_INBOUND.getValue()), // 92
                Collections.singletonList(
                        Long.parseLong(GALTransportStatus.ARRIVED_AT_RACK.getValue()) // 107
                )
        );

        // RELOCATION 순서 유지
        // 94 는 107이 선행되어야함
        PRECONDITION_RULES.put(
                Long.parseLong(GALTransportStatus.ORDER_DONE_RELOCATION.getValue()), // 94
                Collections.singletonList(
                        Long.parseLong(GALTransportStatus.ARRIVED_AT_RACK.getValue()) // 107
                )
        );
    }

    public static List<Long> getRequiredPreTransCodes(Long currentTransCode) {
        return PRECONDITION_RULES.get(currentTransCode);
    }

    public static boolean hasPrecondition(Long currentTransCode) {
        return PRECONDITION_RULES.containsKey(currentTransCode);
    }
}