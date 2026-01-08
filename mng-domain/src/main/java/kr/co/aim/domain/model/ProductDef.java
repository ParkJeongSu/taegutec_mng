package kr.co.aim.domain.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDef {
    private Long id;
    private String productDefName;
    private String description;
    private String productionType;
    private String productionDetailType;
    private String productType;
    private String productSubType;
    private Integer quantity;
    private Integer subQuantity;
    private Integer estimatedCycleTime;
    private Integer xCount;
    private Integer yCount;
    private String sectionName;
    private String techName;
    private String density;
    private String generation;
    private String organization;
    private String checkOutState;
    private LocalDateTime checkOutTime;
    private String checkOutUser;
    private String dataState;
    private String eventName;
    
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;
}
