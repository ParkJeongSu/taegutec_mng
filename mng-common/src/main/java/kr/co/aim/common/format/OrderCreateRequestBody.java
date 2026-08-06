package kr.co.aim.common.format;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateRequestBody {
    private List<ProductionOrderBody> orderList = new ArrayList<>();
}
