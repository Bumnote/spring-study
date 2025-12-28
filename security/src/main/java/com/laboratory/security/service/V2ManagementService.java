package com.laboratory.security.service;

import com.laboratory.security.domain.model.Employee;
import com.laboratory.security.repository.ManagementRepository;
import java.util.List;
import java.util.Map;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.stereotype.Service;

@Service
public class V2ManagementService {

  /* [1] 리포지토리 객체 의존 */
  private final ManagementRepository managementRepository;

  public V2ManagementService(ManagementRepository managementRepository) {
    this.managementRepository = managementRepository;
  }

  /* [2] 직원 리스트 반환 */
  @PostFilter("""
      filterObject.value.managerId() == authentication.principal
      or filterObject.value.id() == authentication.principal""")
  public Map<String, Employee> getMyEmployees() {
    return managementRepository.getAllEmployees();
  }


  /* [3] 특정 직원 정보 반환 */
  @PostAuthorize("""
      returnObject.managerId() == authentication.principal
      or returnObject.id() == authentication.principal
      """)
  public Employee getEmployeeById(String id) {
    return managementRepository.getEmployeeById(id);
  }


  /* [4] 신규 직원 정보 추가 */
  @PreAuthorize("""
      hasAnyRole('MANAGER')
      and #employee.managerId() == authentication.principal""")
  public void addEmployee(Employee employee) {
    managementRepository.addEmployee(employee);
  }

  /* [5] 직원들 정보 추가 */
  @PreAuthorize("hasAnyRole('MANAGER')")
  @PreFilter("filterObject.managerId() == authentication.principal")
  public void addEmployees(List<Employee> employeeList) {
    managementRepository.addEmployees(employeeList);
  }
}