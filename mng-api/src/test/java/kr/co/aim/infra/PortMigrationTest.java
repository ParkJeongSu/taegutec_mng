package kr.co.aim.infra;

import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.PortDefCreateCommand;
import kr.co.aim.domain.model.PortDef;
import kr.co.aim.domain.repository.PortDefRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Disabled
@SpringBootTest(properties = "factory.type=insert")
public class PortMigrationTest {

    @Autowired
    private PortDefRepository portDefRepository; // 또는 JdbcTemplate

    @Test
    @Transactional
    @Commit // 테스트 종료 후 롤백되지 않고 실제 DB에 커밋되도록 설정
    public void migratePortData() {
        // 1. 상대 DB용 DataSource 수동 생성
        DriverManagerDataSource sourceDs = new DriverManagerDataSource();
        sourceDs.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        sourceDs.setUrl("jdbc:sqlserver://192.168.50.135:1433;databaseName=NEXBEMNG;encrypt=false;");
        sourceDs.setUsername("nexbe");
        sourceDs.setPassword("!aim123456");

        JdbcTemplate sourceJdbc = new JdbcTemplate(sourceDs);

        // 2. 상대 DB 조회 및 도메인 객체 생성
        final List<PortDef> insertList = new ArrayList<PortDef>();

        String selectSql = "SELECT equipmentName, portName, factoryName, portNumber, "
                + "       portTransferMode, portType, portDetailType, portUseType, "
                + "       linkEquipmentName, linkPortName "
                + "FROM NEXBEWCS.dbo.PORT";

        sourceJdbc.query(selectSql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                // PortDef 도메인 객체 생성 (생성자 또는 빌더 활용)
                PortDef portDef = new PortDef();
                portDef.setEquipmentName(rs.getString("equipmentName"));
                portDef.setPortName(rs.getString("portName"));
                portDef.setFactoryName(rs.getString("factoryName").toUpperCase());
                portDef.setTransportMode(rs.getString("portTransferMode").toUpperCase());
                portDef.setPortType(rs.getString("portType").toUpperCase());
                portDef.setDetailPortType(rs.getString("portDetailType"));

                insertList.add(portDef);
            }
        });
        TransactionInfo tx = TransactionInfo.now("migratePortData","MNG","");
        // 3. 내 DB에 일괄 저장
        for(PortDef portDef : insertList){
            PortDefCreateCommand command =
                    PortDefCreateCommand
                            .builder()
                            .transactionInfo(tx)
                            .equipmentName(portDef.getEquipmentName())
                            .portName(portDef.getPortName())
                            .factoryName(portDef.getFactoryName())
                            .transportMode(portDef.getTransportMode())
                            .portType(portDef.getPortType())
                            .detailPortType(portDef.getDetailPortType())
                            .build();
            PortDef newPortDef = PortDef.create(command);

            portDefRepository.save(newPortDef);
        }

        System.out.println("마이그레이션 완료 건수: " + insertList.size());
    }
}