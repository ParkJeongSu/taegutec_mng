package kr.co.aim.api.strategy;

import kr.co.aim.api.context.WhatDispatchContext;

public interface WhatDispatchStrategy {

    /**
     * 해당 전략을 적용할 수 있는 조건인지 판단
     */
    boolean supports(WhatDispatchContext context);

    /**
     * 목적지(Target Equipment/Port/Zone)를 계산하여 Context에 설정
     */
    void determineDestination(WhatDispatchContext context);
}
