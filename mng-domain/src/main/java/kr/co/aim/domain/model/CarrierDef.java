package kr.co.aim.domain.model;

import kr.co.aim.common.Utils.TsidUtils;
import kr.co.aim.common.handler.HasTransactionInfo;
import kr.co.aim.domain.command.CarrierDefCreateCommand;
import kr.co.aim.domain.command.CarrierDefUpdateCommand;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CarrierDef implements HasTransactionInfo {

    private Long id;
    private String carrierDefName;
    private String factoryName;
    private String description;
    private String carrierType;
    private String carrierDetailType;
    private Integer defaultCapacity;
    private Integer useCountLimit;
    private Integer useDurationLimit;
    private Integer countLimitPerClean;
    private Integer durationLimitPerClean;
    private Integer cleanCountLimit;
    private String checkOutState;
    private LocalDateTime checkOutTime;
    private String checkOutUser;
    private String dataState;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;
    public static CarrierDef create(CarrierDefCreateCommand command){
        return CarrierDef
                .builder()
                .id(TsidUtils.nextId())
                .carrierDefName(command.getCarrierDefName())
                .factoryName(command.getFactoryName())
                .description(command.getDescription())
                .carrierType(command.getCarrierType())
                .carrierDetailType(command.getCarrierDetailType())
                .defaultCapacity(command.getDefaultCapacity())
                .useCountLimit(command.getUseCountLimit())
                .useDurationLimit(command.getUseDurationLimit())
                .countLimitPerClean(command.getCountLimitPerClean())
                .durationLimitPerClean(command.getDurationLimitPerClean())
                .cleanCountLimit(command.getCleanCountLimit())
                .checkOutState(command.getCheckOutState())
                .checkOutTime(command.getCheckOutTime())
                .checkOutUser(command.getCheckOutUser())
                .dataState(command.getDataState())
                .eventName(command.getTransactionInfo().eventName())
                .eventTime(command.getTransactionInfo().eventTime())
                .eventUser(command.getTransactionInfo().eventUser())
                .eventComment(command.getTransactionInfo().eventComment())
                .build();
    }

    public CarrierDef update(CarrierDefUpdateCommand command) {
        this.setFactoryName(command.getFactoryName());
        this.setDescription(command.getDescription());
        this.setCarrierType(command.getCarrierType());
        this.setCarrierDetailType(command.getCarrierDetailType());
        this.setDefaultCapacity(command.getDefaultCapacity());
        this.setUseCountLimit(command.getUseCountLimit());
        this.setUseDurationLimit(command.getUseDurationLimit());
        this.setCountLimitPerClean(command.getCountLimitPerClean());
        this.setDurationLimitPerClean(command.getDurationLimitPerClean());
        this.setCleanCountLimit(command.getCleanCountLimit());
        this.setCheckOutState(command.getCheckOutState());
        this.setCheckOutTime(command.getCheckOutTime());
        this.setCheckOutUser(command.getCheckOutUser());
        this.setDataState(command.getDataState());
        this.setEventName(command.getTransactionInfo().eventName());
        this.setEventTime(command.getTransactionInfo().eventTime());
        this.setEventUser(command.getTransactionInfo().eventUser());
        this.setEventComment(command.getTransactionInfo().eventComment());
        return this;
    }

}
