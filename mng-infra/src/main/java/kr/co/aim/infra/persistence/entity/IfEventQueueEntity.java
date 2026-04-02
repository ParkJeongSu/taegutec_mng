package kr.co.aim.infra.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "IF_EVENT_QUEUE")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IfEventQueueEntity {
    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "EVENT_TYPE")
    private String eventType;

    @Column(name = "PAYLOAD")
    private String payload;

    @Column(name = "IF_STATUS")
    private String ifStatus;

    @Column(name = "CARRIER_NAME")
    private String carrierName;

    @Column(name = "IDOC_ID")
    private String idocId;

    @Column(name = "ORDER_ID")
    private String orderId;

    @Column(name = "ORDER_LINE_NUMBER")
    private String orderLineNumber;

    @Column(name = "RETRY_CNT")
    private Integer retryCNT;

    @Column(name = "ERR_MSG")
    private String errMSG;

    @Column(name = "CREATE_TIME")
    private LocalDateTime createTime;

    @Column(name = "UPDATE_TIME")
    private LocalDateTime updateTime;

}
