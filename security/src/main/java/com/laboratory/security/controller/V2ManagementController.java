package com.laboratory.security.controller;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v2/management")
public class V2ManagementController {

  private final AtomicInteger id = new AtomicInteger(0);
  private final Map<Integer, String> employees;

  public V2ManagementController() {
    employees = new ConcurrentHashMap<>();
    employees.put(id.incrementAndGet(), "junhyunny"); // 시스템 관리자
    employees.put(id.incrementAndGet(), "tangerine"); // 매니저
    employees.put(id.incrementAndGet(), "jua"); // 직원(쓰기, 읽기)
    employees.put(id.incrementAndGet(), "tory"); // 직원(읽기)
  }

  /* [2] 관리자 정보 반환 */
  @GetMapping("/admin")
  public String systemAdmin() {
    return "Junhyunny";
  }

  /* [3] 직원 리스트 반환 */
  @GetMapping("/employees")
  public Map<Integer, String> getAllEmployees() {
    return employees;
  }

  /* [4] 특정 직원 정보 반환 */
  @GetMapping("/employees/{id}")
  public String getEmployeeById(
      @AuthenticationPrincipal User user,
      @PathVariable("id") Integer id
  ) {
    var name = employees.get(id);
    if (name == null) {
      throw new RuntimeException("employee not found");
    }
    return String.format(
        "%s searches %s(id: %s)'s employee information\n",
        user.getUsername(),
        name,
        id
    );
  }

  /* [5] 직원 정보 추가 */
  @PostMapping("/employees")
  public void addEmployee(
      @AuthenticationPrincipal User user,
      @RequestBody String name
  ) {
    var newId = id.incrementAndGet();
    employees.put(newId, name);
    System.out.printf(
        "%s creates %s(id: %s)'s employee information\n",
        user.getUsername(),
        name,
        newId
    );
  }

  /* [6] 직원 정보 삭제 */
  @DeleteMapping("/employees/{id}")
  public void deleteEmployee(
      @AuthenticationPrincipal User user,
      @PathVariable("id") Integer id
  ) {
    var name = employees.remove(id);
    if (name == null) {
      throw new RuntimeException("employee not found");
    }
    System.out.printf(
        "%s deletes %s(id: %s)'s employee information\n",
        user.getUsername(),
        name,
        id
    );
  }

}
