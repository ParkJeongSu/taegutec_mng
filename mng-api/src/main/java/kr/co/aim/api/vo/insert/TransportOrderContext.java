package kr.co.aim.api.vo.insert;

import kr.co.aim.domain.model.TransportOrder;
import kr.co.aim.infra.persistence.db2entity.insert.H2OrderDEntity;
import kr.co.aim.infra.persistence.db2entity.insert.H2OrderMEntity;
import kr.co.aim.infra.persistence.db2entity.insert.IdocEntity;
import kr.co.aim.infra.persistence.entity.TransportOrderEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class TransportOrderContext {
    private final TransportOrder transportOrder; // MSSQL 엔티티 (조회 시점에 없을 수 있음)
    private final IdocEntity idoc;                     // DB2 IDOC
    private final H2OrderMEntity master;               // DB2 Master
    private final List<H2OrderDEntity> details;        // DB2 Details

    // 리스트 중 첫 번째 디테일을 반환 (단건 처리용)
    public H2OrderDEntity getFirstDetail() {
        if (details == null || details.isEmpty()) return null;
        return details.get(0);
    }
    public boolean isRelocation(){
        if(details == null || details.isEmpty()) {
            return false;
        }
        else if(details.size() == 1) {
            return false;
        }
        else if(details.size() == 2){
            return true;
        }
        else{
            return false;
        }
    }
}
