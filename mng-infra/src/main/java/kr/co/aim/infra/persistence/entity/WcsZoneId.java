package kr.co.aim.infra.persistence.entity;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class WcsZoneId implements Serializable {

    private static final long serialVersionUID = 1L;

    private String factoryName;
    private String zoneName;
}
