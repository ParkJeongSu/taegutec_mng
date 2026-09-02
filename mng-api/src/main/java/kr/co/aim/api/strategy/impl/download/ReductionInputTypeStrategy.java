package kr.co.aim.api.strategy.impl.download;

import kr.co.aim.api.service.LotCarrierMappingService;
import kr.co.aim.api.strategy.DownloadStrategy;
import kr.co.aim.api.context.DownloadContext;
import kr.co.aim.common.Utils.FormatUtils;
import kr.co.aim.common.enums.*;
import kr.co.aim.common.format.CarrierInfoDownloadSendBody;
import kr.co.aim.common.format.RecipeBody;
import kr.co.aim.common.format.RecipeParameterListBody;
import kr.co.aim.common.format.RecipeRequestBody;
import kr.co.aim.common.format.request.BaseMessage;
import kr.co.aim.domain.model.EquipmentDef;
import kr.co.aim.domain.model.LotCarrierMapping;
import kr.co.aim.domain.model.PortDef;
import kr.co.aim.domain.repository.LotCarrierMappingRepository;
import kr.co.aim.infra.config.RabbitConfig;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ReductionInputTypeStrategy implements DownloadStrategy {

    private final LotCarrierMappingRepository lotCarrierMappingRepository;
    private final RabbitTemplate rabbitTemplate;


    @Override
    public boolean supports(DownloadContext context) {
        PortDef portDef = context.getPortDef();
        EquipmentDef equipmentDef = context.getEquipmentDef();
        // 환원로 설비이면서 INPUT port 인 경우
        return StringUtils.equals(PortType.INPUT.getValue(), portDef.getPortType())
                && StringUtils.equals(EquipmentDetailType.REDUCTION.getValue(), equipmentDef.getDetailEquipmentType());
    }

    @Override
    public BaseMessage<CarrierInfoDownloadSendBody> determineCarrierInfo(DownloadContext context) {

        Optional<LotCarrierMapping> optionalLotCarrierMapping = lotCarrierMappingRepository.findByCarrierName(context.getCarrier().getCarrierName());

        if (optionalLotCarrierMapping.isEmpty()) {
            throw new RuntimeException("Carrier not found");
        }

        LotCarrierMapping lotCarrierMapping = optionalLotCarrierMapping.get();

        if(lotCarrierMapping.getSeq()==1){
            String transactionId = FormatUtils.getTransactionId(context.getTx().eventTime());

            BaseMessage<RecipeRequestBody> mantiRequestMessage = new BaseMessage<>();
            mantiRequestMessage.setMessageName(MessageList.RECIPE_REQUEST.getMessageName());
            mantiRequestMessage.setTransactionId(transactionId);
            mantiRequestMessage.setMessageFrom(SystemName.MNG.getValue());
            mantiRequestMessage.setMessageOwner(SystemName.MNG.getValue());
            mantiRequestMessage.setMessageTo(SystemName.MANTI.getValue());
            mantiRequestMessage.setEventTime(transactionId);
            mantiRequestMessage.setResultMessage("");
            mantiRequestMessage.setResultCode(ResultCode.OK.getValue());
            RecipeRequestBody recipeRequestBody =
                    RecipeRequestBody
                            .builder()
                            .equipmentName(context.getEquipment().getEquipmentName())
                            .portName(context.getPort().getPortName())
                            .carrierName(context.getCarrier().getCarrierName())
                            .orderId(lotCarrierMapping.getOrderId())
                            .orderLineNumber(lotCarrierMapping.getOrderLineNumber())
                            .transactionId(lotCarrierMapping.getMngKey().toString())
                            .build();

            mantiRequestMessage.setBody(recipeRequestBody);

            rabbitTemplate.convertAndSend(
                    RabbitConfig.EXCHANGE_MANTI,
                    RabbitConfig.ROUTING_MANTI,
                    mantiRequestMessage
            );
            return null;
        }
        else
        {
            List<RecipeParameterListBody> recipeParameterList = new ArrayList<>();
            RecipeBody recipeBody = RecipeBody
                    .builder()
                    .recipeName("")
                    .parameterList(recipeParameterList)
                    .build();

            CarrierInfoDownloadSendBody body = CarrierInfoDownloadSendBody
                    .builder()
                    .equipmentName(context.getEquipment().getEquipmentName())
                    .portName(context.getPort().getPortName())
                    .carrierName(context.getCarrier().getCarrierName())
                    .recipe(recipeBody)
                    .build();

            return createCarrierInfoDownloadSendMessage(context.getTx(),body);
        }

    }
}