package kr.co.aim.api.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
@Disabled
@SpringBootTest
@ActiveProfiles({"scheduler"})
class InsertSimulatorFacadeTest {

    @Autowired
    private InsertSimulatorFacade insertSimulatorFacade;

    @Test
    void test1(){
        //dataTransferService.transferUsersToDb2();
    }

}