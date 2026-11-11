package kr.co.aim.common.format;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventQueueListReportBody {
    private List<EventQueueReportBody> list;
}
