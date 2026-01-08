package kr.co.aim.common.format;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class Header {
    private String messageName;
    private String transactionId;
    private String systemName;
    private String timestamp;
    private String version;
    private String replyQueueName;
    private String eventUser;
    private String eventComment;
    // Getter and Setter
}