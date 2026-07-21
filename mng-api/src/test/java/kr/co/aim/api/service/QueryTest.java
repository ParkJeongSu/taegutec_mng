package kr.co.aim.api.service;

import kr.co.aim.common.enums.DetailPortType;
import kr.co.aim.common.enums.PortTransportState;
import kr.co.aim.domain.model.Port;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest(properties = "factory.type=insert")
@Transactional // 테스트 완료 후 DB를 자동으로
class QueryTest {

    @Autowired
    private PortService portService;

    @Test
    void loadRequestPortQuery() {
        // Given: 테이블이 비어있는 상태 (또는 1번이 없는 상태)
        // @Transactional 덕분에 매 테스트마다 깨끗한 상태 혹은 롤백이 보장됩니다.
        // When: ID 발행 요청
        // Then: 결과가 1인지 검증 (System.out 대신 assertEquals 사용)


        List<String> detailPortTypes = new ArrayList<>();
        detailPortTypes.add(DetailPortType.CRANE_OUT_PND.getValue());
        detailPortTypes.add(DetailPortType.CRANE_BOTH_PND.getValue());
        List<Port> portList = portService.findEarliestPortPerWorkCenter(PortTransportState.READY_TO_LOAD.getValue(),detailPortTypes);

    }

}