package kr.co.aim.api.dto.powder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class RawMaterialReceivingStartRequest {
    List<RawMaterialReceivingStart> list;
}
