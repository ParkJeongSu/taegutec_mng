package kr.co.aim.api.strategy;

import kr.co.aim.api.context.WhereDispatchContext;

public interface WhereDispatchStrategy {

    /**
     * 해당 전략을 적용할 수 있는 조건인지 판단
     */
    boolean supports(WhereDispatchContext context);

    /**
     * 목적지(Target Equipment/Port/Zone)를 계산하여 Context에 설정
     */
    void determineDestination(WhereDispatchContext context);
}
