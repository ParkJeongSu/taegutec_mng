package kr.co.aim.common.format;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductionOrder {
    private Long id;
    private String orderId;
    private String orderLineNumber;
    private String lotName;
    private String description;
    private String itemName;
    private String recipeName;
    private String carrierName;
    private Long idocId;
    private Long h2OrderDpLineId;
    private String galKey;
    private String productionOrderType;
    private String productionOrderState;
    private String reportState;
    private String holdState;
    private String reasonCode;
    private String equipmentName;
    private BigDecimal planQuantity;
    private BigDecimal releasedQuantity;
    private BigDecimal startedQuantity;
    private BigDecimal endedQuantity;
    private BigDecimal scrappedQuantity;
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "Asia/Seoul")
    private LocalDateTime createTime;
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "Asia/Seoul")
    private LocalDateTime releaseTime;
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "Asia/Seoul")
    private LocalDateTime completeTime;
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "Asia/Seoul")
    private LocalDateTime validationTime;
    private String createUser;
    private String releaseUser;
    private String completeUser;
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "Asia/Seoul")
    private LocalDateTime dueDate;
    private String eventName;
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "Asia/Seoul")
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;
}
