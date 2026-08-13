package kr.co.aim.api.strategy;

import kr.co.aim.api.context.ProcessJobStartedContext;

public interface ProcessJobStartedStrategy {

    /**
     * 해당 전략을 적용할 수 있는 조건인지 판단
     */
    boolean supports(ProcessJobStartedContext context);

    /**
     * context 에 따른 로직 분기
     */
    void processJobStarted(ProcessJobStartedContext context);

}
