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
@Table(name = "IF_EVENT_LOG")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IF_EVENT_LOGEntity {
    @Id
    @Column(name = "SEQ")
    private Long seq;

    @Column(name = "EVENT_TYPE")
    private String eventType;

    @Column(name = "PAYLOAD")
    private String payload;

    @Column(name = "IF_STATUS")
    private String ifStatus;

    @Column(name = "CARRIER_NAME")
    private String carrierName;

    @Column(name = "IDOC_ID")
    private Long idocId;

    @Column(name = "RETRY_CNT")
    private Integer retryCNT;

    @Column(name = "ERR_MSG")
    private String errMSG;

    @Column(name = "CREATE_TIME")
    private LocalDateTime createTime;

    @Column(name = "UPDATE_TIME")
    private LocalDateTime updateTime;

}
