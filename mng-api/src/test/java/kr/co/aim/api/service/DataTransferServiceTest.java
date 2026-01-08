package kr.co.aim.api.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
@Disabled
@SpringBootTest
@ActiveProfiles({"scheduler"})
class DataTransferServiceTest {

    @Autowired
    private DataTransferService dataTransferService;

    @Test
    void test1(){
        //dataTransferService.transferUsersToDb2();
    }

}