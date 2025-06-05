package study.actuator.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class LogController {

  @GetMapping("/log")
  public String log() {
    log.trace("This is a TRACE log message");
    log.debug("This is a DEBUG log message");
    log.info("This is an INFO log message");
    log.warn("This is a WARN log message");
    log.error("This is an ERROR log message");
    return "ok";
  }

}
