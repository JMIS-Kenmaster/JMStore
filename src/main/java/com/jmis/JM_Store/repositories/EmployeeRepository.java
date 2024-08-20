package com.jmis.JM_Store.repositories;

import com.jmis.JM_Store.models.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    boolean existsByEmployeeNumber(String employeeNumber);

    Optional<Employee> findByName(String name);

    Optional<Employee> findByEmployeeNumber(String employeeNumber);
}
