package kr.co.aim.common.format;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderReleaseRequestBody {
    private Long id;
    private String orderId;

}
