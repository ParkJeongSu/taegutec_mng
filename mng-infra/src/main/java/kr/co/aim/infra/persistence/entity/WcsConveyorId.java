package kr.co.aim.infra.persistence.entity;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class WcsConveyorId implements Serializable {

    private static final long serialVersionUID = 1L;

    private String conveyorGroup;
    private String conveyorName;
    private Integer conveyorNumber;
    private String factoryName;
    private Integer localNo;
}
