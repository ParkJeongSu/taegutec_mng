package kr.co.aim.infra;

import kr.co.aim.domain.model.CarrierDef;
import kr.co.aim.infra.persistence.adapter.CarriersDefRepositoryImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest()
@Transactional
@ActiveProfiles({"pex"})
class CarrierDefRepositoryImplTest {

    @Autowired
    private CarriersDefRepositoryImpl carriersDefRepository;

    @Test
    void getCarrierDefTest(){
        List<CarrierDef> result =  carriersDefRepository.findAll();
        assertNotNull(result);
        System.out.println("조회된 건수:" +result.size());
        for(CarrierDef c : result)
        {
            System.out.println(c);
        }
        System.out.println("테스트 완료");
    }

}