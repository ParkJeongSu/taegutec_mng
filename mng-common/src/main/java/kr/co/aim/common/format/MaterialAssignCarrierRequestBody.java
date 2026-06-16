package kr.co.aim.common.format;

import lombok.*;

import java.util.List;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialAssignCarrierRequestBody {
    private Long id;
    private String carrierName;
    private String orderId;
    private List<MaterialList> materialList;
}
