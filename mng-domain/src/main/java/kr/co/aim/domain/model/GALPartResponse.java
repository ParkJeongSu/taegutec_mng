package kr.co.aim.domain.model;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class GALPartResponse {

    private Long lineId;
    private Long idocId;
    private LocalDateTime dtimeCre;
    private LocalDateTime dtimeMod;
    private String usrMod;
    private String pgmMod;
    private Integer modCnt;
    private String cPartId;
    private String cPartDsc;
    private String cPartDsc2;
    private BigDecimal cratIo;
}
