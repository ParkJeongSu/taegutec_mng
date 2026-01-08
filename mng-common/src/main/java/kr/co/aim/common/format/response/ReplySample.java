package kr.co.aim.common.format.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Builder
@Data
@NoArgsConstructor //Jackson 역직렬화를 위해 기본생성자가 반드시 필요합니다.
@AllArgsConstructor
public class ReplySample {
    private String messageName;
    private String messageContent;
}
