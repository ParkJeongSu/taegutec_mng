package kr.co.aim.api.vo.transportJob;

import kr.co.aim.domain.command.TransportJobCreateCommand;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
@Builder
public class CreateTransportJobVo {
    private final List<TransportJobCreateCommand> transportJobCreateCommandList;

}