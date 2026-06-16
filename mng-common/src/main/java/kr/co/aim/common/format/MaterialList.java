package kr.co.aim.common.format;

import lombok.*;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialList {
    private String materialName;
    private String materialType;
    private String quantity;
    private String item;
    private String lotName;
}