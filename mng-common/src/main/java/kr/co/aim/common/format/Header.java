package kr.co.aim.common.format;

import lombok.*;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
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