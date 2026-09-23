package kr.co.aim.infra.persistence.entity;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class WcsAlarmId implements Serializable {

    private static final long serialVersionUID = 1L;

    private String alarmId;
    private String equipmentName;
    private String factoryName;
    private Integer layerNumber;
    private String layerType;
}
