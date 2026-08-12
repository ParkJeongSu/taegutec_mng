package kr.co.aim.api.strategy;

import kr.co.aim.api.vo.powder.ops.DownloadContext;
import kr.co.aim.api.vo.powder.ops.ProductionOrderProcessContext;
import kr.co.aim.common.Utils.FormatUtils;
import kr.co.aim.common.enums.MessageList;
import kr.co.aim.common.enums.ResultCode;
import kr.co.aim.common.enums.SystemName;
import kr.co.aim.common.format.CarrierInfoDownloadSendBody;
import kr.co.aim.common.format.request.BaseMessage;
import kr.co.aim.common.record.TransactionInfo;

public interface ProductionOrderProcessStrategy {

    /**
     * 해당 전략을 적용할 수 있는 조건인지 판단
     */
    boolean supports(ProductionOrderProcessContext context);

    /**
     * context 에 따른 로직 분기
     */
    void productionOrderProcess(ProductionOrderProcessContext context);

}
