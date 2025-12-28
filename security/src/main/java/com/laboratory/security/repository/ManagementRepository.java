package com.laboratory.security.repository;

import com.laboratory.security.domain.model.Employee;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class ManagementRepository {

  private final Map<String, Employee> employees;

  public ManagementRepository() {
    this.employees = new ConcurrentHashMap<>();
    init();
  }

  /* [1] 가상 직원 리스트 준비 */
  public void init() {
    this.employees.put("S0001", new Employee("S0001", "S0001", "supervisor"));
    this.employees.put("M0001", new Employee("M0001", "S0001", "junhyunny"));
    this.employees.put("M0002", new Employee("M0002", "S0001", "tangerine"));
    this.employees.put("E0001", new Employee("E0001", "M0001", "jua"));
    this.employees.put("E0002", new Employee("E0002", "M0002", "tory"));
  }

  /* [2] 모든 직원 리스트 반환 */
  public Map<String, Employee> getAllEmployees() {
    return employees;
  }

  /* [3] 직원 아이디로 조회 */
  public Employee getEmployeeById(String id) {
    var employee = employees.get(id);
    if (employee == null) {
      throw new RuntimeException("employee not found");
    }
    return employee;
  }

  /* [4] 직원 추가 */
  public void addEmployee(Employee employee) {
    employees.put(employee.id(), employee);
  }

  /* [5] 여러 명의 직원을 추가 */
  public void addEmployees(List<Employee> employeeList) {
    for (var employee : employeeList) {
      employees.put(employee.id(), employee);
    }
  }

}

