package kr.co.aim.api.context;


import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.model.*;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DownloadContext {


    private final EquipmentDef equipmentDef;
    private final Equipment equipment;
    private final PortDef portDef;
    private final Port port;
    private final CarrierDef carrierDef;
    private final Carrier carrier;
    private final TransactionInfo tx;


}
