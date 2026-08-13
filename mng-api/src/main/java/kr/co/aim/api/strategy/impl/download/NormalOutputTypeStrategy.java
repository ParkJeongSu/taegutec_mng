package kr.co.aim.api.strategy.impl.download;

import kr.co.aim.api.strategy.DownloadStrategy;
import kr.co.aim.api.context.DownloadContext;
import kr.co.aim.common.enums.PortType;
import kr.co.aim.common.enums.PortUseType;
import kr.co.aim.common.format.CarrierInfoDownloadSendBody;
import kr.co.aim.common.format.RecipeBody;
import kr.co.aim.common.format.RecipeParameterListBody;
import kr.co.aim.common.format.request.BaseMessage;
import kr.co.aim.domain.model.PortDef;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class NormalOutputTypeStrategy implements DownloadStrategy {


    @Override
    public boolean supports(DownloadContext context) {
        PortDef portDef = context.getPortDef();
        if(StringUtils.equals(PortType.INPUT.getValue(),portDef.getPortType())){
            return false;
        }

        if(StringUtils.equals(PortUseType.NG.getValue(),portDef.getPortUseType())){
            return false;
        }
        return true;
    }

    @Override
    public BaseMessage<CarrierInfoDownloadSendBody> determineCarrierInfo(DownloadContext context) {

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