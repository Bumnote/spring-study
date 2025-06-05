package study.actuator.controller;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class TrafficController {

  // CPU 사용량 증가 테스트
  @GetMapping("cpu")
  public String cpu() {
    log.info("cpu");
    long value = 0L;

    for (long i = 0; i < 100_000_000_000L; i++) {
      value++;
    }

    return "ok value = " + value;
  }

  // JVM 메모리 사용량 증가 테스트
  private List<String> list = new ArrayList<>();

  @GetMapping("jvm")
  public String jvm() {
    log.info("jvm");
    for (int i = 0; i < 1_000_000; i++) {
      list.add("hello jvm! " + i);
    }

    return "ok";
  }

  // JDBC 연결 테스트
  @Autowired
  DataSource dataSource;

  @GetMapping("/jdbc")
  public String jdbc() throws SQLException {
    log.info("jdbc");
    Connection conn = dataSource.getConnection();
    log.info("connection info= {}", conn);

    // conn.close(); // Uncomment this line to close the connection after use
    return "ok";
  }

  // error 로그 테스트
  @GetMapping("/error-log")
  public String errorLog() {
    log.error("error log");
    return "error";
  }
}
