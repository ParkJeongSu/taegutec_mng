package kr.co.aim.api.service;

import kr.co.aim.domain.model.User;
import kr.co.aim.domain.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Profile({"scheduler"})
public class DataTransferServiceOld {

    private final UserRepository userRepository; // MSSQL 데이터 접근
    private final JdbcTemplate db2JdbcTemplate; // DB2 데이터 접근

    // Lombok 대신 직접 생성자를 작성하고, 파라미터에 @Qualifier를 붙여줍니다.
    public DataTransferServiceOld(UserRepository userRepository,
                                  @Qualifier("db2JdbcTemplate") JdbcTemplate db2JdbcTemplate) {
        this.userRepository = userRepository;
        this.db2JdbcTemplate = db2JdbcTemplate;
    }

    @Transactional
    public void transferUsersToDb2() {
        log.info("Starting data transfer from MSSQL to DB2");

        // 1. MSSQL에서 모든 사용자 데이터 읽기
        List<User> users = userRepository.findAll().stream().toList();

        log.info("Found {} users in MSSQL to transfer.", users.size());

        // 2. DB2에 데이터 삽입
        // DB2 테이블 이름과 컬럼명은 예시입니다. 실제 테이블 구조에 맞게 수정해야 합니다.
        String insertSql = "INSERT INTO USERS (ID, NAME, EMAIL) VALUES (?, ?, ?)";

        try {
            int[][] batchInsertResult = db2JdbcTemplate.batchUpdate(insertSql, users, users.size(),
                    (ps, user) -> {
                        ps.setLong(1, user.getId());
                        ps.setString(2, user.getUserName());
                        ps.setString(3, user.getEmail());
                    });
            log.info("Successfully transferred {} users to DB2.", batchInsertResult.length);
        } catch (Exception e) {
            log.info("exception logic start");
            Throwable t = e;
            while (t != null) {
                if (t instanceof java.sql.SQLException se) {
                    System.err.printf("DB2 ERROR: code=%d state=%s msg=%s%n",
                            se.getErrorCode(), se.getSQLState(), se.getMessage());
                    if (se instanceof java.sql.BatchUpdateException be) {
                        System.err.println("BATCH COUNTS=" + java.util.Arrays.toString(be.getUpdateCounts()));
                    }
                    t = se.getNextException(); // ★ 다음 원인
                } else t = t.getCause();
            }
        }
    }
}