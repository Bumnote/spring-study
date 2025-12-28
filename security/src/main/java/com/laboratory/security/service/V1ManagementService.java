package com.laboratory.security.service;

import com.laboratory.security.domain.model.Employee;
import com.laboratory.security.repository.ManagementRepository;
import java.util.List;
import java.util.Map;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@PreAuthorize("denyAll()")
@Service
public class V1ManagementService {

  private final ManagementRepository managementRepository;

  public V1ManagementService(ManagementRepository managementRepository) {
    this.managementRepository = managementRepository;
  }

  /* [2] 직원 리스트 반환 */
  public Map<String, Employee> getMyEmployees() {
    return managementRepository.getAllEmployees();
  }

  /* [3] 특정 직원 정보 반환 */
  public Employee getEmployeeById(String id) {
    return managementRepository.getEmployeeById(id);
  }

  /* [4] 신규 직원 정보 추가 */
  public void addEmployee(Employee employee) {
    managementRepository.addEmployee(employee);
  }

  /* [5] 직원들 정보 추가 */
  public void addEmployees(List<Employee> employeeList) {
    managementRepository.addEmployees(employeeList);
  }
}