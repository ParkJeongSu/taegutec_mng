package kr.co.aim.api.eziframe.controller;

//import ezieco.eziframe.middleware.event.data.BaseMessage;
//import ezieco.eziframe.middleware.event.data.Header;
//import ezieco.eziframe.middleware.event.utils.EziController;
//import ezieco.eziframe.middleware.event.utils.EziListener;
//import ezieco.eziframe.middleware.event.utils.MessageBody;
//import ezieco.eziframe.middleware.event.utils.MessageHeader;
//import kr.co.aim.common.format.LoadCompletedBody;
//import kr.co.aim.common.format.LoadRequestBody;
//import kr.co.aim.common.format.PortStateReportBody;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
////@EziController
//@RequiredArgsConstructor
//public class MessageQueueController {
//
//
//    @EziListener(messageName = "loadComplete")
//    public void loadComplete( BaseMessage<LoadCompletedBody>  baseMessage) {
//        log.info("received. message={}", baseMessage);
//    }
//
////    @EziListener(messageName = "loadComplete")
////    public void loadComplete( @MessageBody LoadCompletedBody baseMessage) {
////        log.info("received. message={}", baseMessage);
////    }
//
//    @EziListener(messageName = "loadComplete2")
//    public void loadComplete2(BaseMessage<LoadCompletedBody> baseMessage) {
//        log.info("received. message={}", baseMessage);
//    }
//
//
//    @EziListener(messageName = "LoadRequest")
//    public void loadRequest(@MessageBody LoadRequestBody loadRequest, @MessageHeader Header header) {
//        log.info("received. messageName={}, body={}", header.getMessageName(), loadRequest);
//    }
//
//    @EziListener(messageName = "PortStatusRequest")
//    public void portStatusRequest(BaseMessage<PortStateReportBody> baseMessage) {
//
//    	PortStateReportBody portStatusRequest = baseMessage.getBody();
//    }
//}
