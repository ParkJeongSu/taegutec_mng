package kr.co.aim.api.strategy;

import kr.co.aim.api.context.DownloadContext;
import kr.co.aim.common.Utils.FormatUtils;
import kr.co.aim.common.enums.MessageList;
import kr.co.aim.common.enums.ResultCode;
import kr.co.aim.common.enums.SystemName;
import kr.co.aim.common.format.CarrierInfoDownloadSendBody;
import kr.co.aim.common.format.request.BaseMessage;
import kr.co.aim.common.record.TransactionInfo;

public interface DownloadStrategy {

    /**
     * 해당 전략을 적용할 수 있는 조건인지 판단
     */
    boolean supports(DownloadContext context);

    /**
     * context 에 따른 로직 분기
     */
    BaseMessage<CarrierInfoDownloadSendBody> determineCarrierInfo(DownloadContext context);

    /**
     * 기본적인 메시지 생성
     */
    default BaseMessage<CarrierInfoDownloadSendBody> createCarrierInfoDownloadSendMessage(TransactionInfo tx, CarrierInfoDownloadSendBody body) {
        String transactionId = FormatUtils.getTransactionId(tx.eventTime());

        BaseMessage<CarrierInfoDownloadSendBody> request = new BaseMessage<>();
        request.setTransactionId(transactionId);
        request.setMessageFrom(SystemName.MNG.getValue());
        request.setMessageOwner(SystemName.MNG.getValue());
        request.setMessageTo(SystemName.WCS.getValue());
        request.setEventTime(transactionId);
        request.setMessageName(MessageList.CARRIER_INFO_DOWNLOAD_SEND.getMessageName());
        request.setResultCode(ResultCode.OK.getValue());
        request.setBody(body);

        return request;
    }
}
