package kr.co.aim.api.strategy;

import kr.co.aim.api.vo.powder.ops.ProcessJobEndedContext;

public interface ProcessJobEndedStrategy {

    /**
     * 해당 전략을 적용할 수 있는 조건인지 판단
     */
    boolean supports(ProcessJobEndedContext context);

    /**
     * context 에 따른 로직 분기
     */
    void processJobEnded(ProcessJobEndedContext context);

}
