package kr.co.aim.common.format;

import lombok.*;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderReleaseReplyBody {
    private Long id;
    private String orderId;

}
