package kr.co.aim.common.format;

import lombok.*;

import java.util.List;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransportJobRequestListBody {
    private List<TransportJobRequestBody> transportJobList;
}
