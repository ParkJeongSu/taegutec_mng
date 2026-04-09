package kr.co.aim.api.vo.insert.ops;

import lombok.*;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransportCancelReasonVo {
    private String code;
    private String message;
}