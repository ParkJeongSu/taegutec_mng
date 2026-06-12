package kr.co.aim.domain.model;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter // MapStruct와 JPA 바인딩을 위해 Setter 제공 (또는 @Builder/@AllArgsConstructor)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode // 복합키 필수 항목
public class IdProductivityDaily implements Serializable {
    private String statDate;
    private String equipmentName;

}