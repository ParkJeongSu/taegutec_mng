package kr.co.aim.domain.model;

import kr.co.aim.common.Utils.TsidUtils;
import kr.co.aim.common.handler.HasTransactionInfo;
import kr.co.aim.domain.command.ProductDefCreateCommand;
import kr.co.aim.domain.command.ProductDefUpdateCommand;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ProductDef implements HasTransactionInfo {
    private Long id;
    private String productDefName;
    private String factoryName;
    private String description1;
    private String description2;
    private BigDecimal ratio;
    private BigDecimal defaultReceiveQuantity;
    private BigDecimal toleranceVal;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;

    public static ProductDef create(ProductDefCreateCommand command) {
        return ProductDef
                .builder()
                .id(TsidUtils.nextId())
                .productDefName(command.getProductDefName())
                .factoryName(command.getFactoryName())
                .description1(command.getDescription1())
                .description2(command.getDescription2())
                .ratio(command.getRatio())
                .defaultReceiveQuantity(command.getDefaultReceiveQuantity())
                .toleranceVal(command.getToleranceVal())
                .eventName(command.getTransactionInfo().eventName())
                .eventTime(command.getTransactionInfo().eventTime())
                .eventUser(command.getTransactionInfo().eventUser())
                .eventComment(command.getTransactionInfo().eventComment())
                .build();

    }

    public ProductDef update(ProductDefUpdateCommand command) {
        setFactoryName(command.getFactoryName());
        setDescription1(command.getDescription1());
        setDescription2(command.getDescription2());
        setRatio(command.getRatio());
        setDefaultReceiveQuantity(command.getDefaultReceiveQuantity());
        setEventName(command.getTransactionInfo().eventName());
        setEventTime(command.getTransactionInfo().eventTime());
        setEventUser(command.getTransactionInfo().eventUser());
        setEventComment(command.getTransactionInfo().eventComment());
        setToleranceVal(command.getToleranceVal());
        return this;
    }
}