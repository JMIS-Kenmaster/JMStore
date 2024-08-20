package com.jmis.JM_Store.services;

import com.jmis.JM_Store.models.Employee;
import com.jmis.JM_Store.repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
@Service
public class EmployeeService implements UserDetailsService {

    @Autowired
    private EmployeeRepository repo;

    @Override
    public UserDetails loadUserByUsername(String employeeNumber) throws UsernameNotFoundException {
        Optional<Employee> employeeOptional = repo.findByEmployeeNumber(employeeNumber);

        if (employeeOptional.isPresent()) {
            Employee employee = employeeOptional.get();
            List<String> roles = new ArrayList<>();
            roles.add("USER");  // default role

            if (employee.isAdmin()) {
                roles.add("ADMIN");
            }
            if (employee.isSuperUser()) {
                roles.add("SUPERUSER");
            }

            return User.withUsername(employee.getEmployeeNumber())
                    .password(employee.getPassword())
                    .authorities(roles.toArray(new String[0]))
                    .build();
        }
        throw new UsernameNotFoundException("Employee not found");
    }

    public Optional<Employee> findByEmployeeNumber(String employeeNumber) {
        return repo.findByEmployeeNumber(employeeNumber);
    }

    public Optional<Employee> findByName(String name) {
        return repo.findByName(name);
    }

    public List<Employee> findAll() {
        return repo.findAll();
    }

    public Optional<Employee> findById(Integer id) {
        return repo.findById(id);
    }

    public void save(Employee employee) {
        // Hash the password before saving
        if (employee.getPassword() != null && !employee.getPassword().isEmpty()) {
            employee.setPassword(new BCryptPasswordEncoder().encode(employee.getPassword()));
        }
        repo.save(employee);
    }

    public String generateUniqueEmployeeNumber() {
        String prefix = "265";
        String number;
        do {
            number = prefix + (1000000 + new Random().nextInt(9000000)); // Generates a number in the range 265000000 to 265999999
        } while (repo.existsByEmployeeNumber(number)); // Ensure the number is unique
        return number;
    }
}
