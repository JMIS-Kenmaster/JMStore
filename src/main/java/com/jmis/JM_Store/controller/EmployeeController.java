package com.jmis.JM_Store.controller;

import com.jmis.JM_Store.models.Employee;
import com.jmis.JM_Store.models.EmployeeDto;
import com.jmis.JM_Store.services.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/dashboard")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/employees/home")
    @Secured({"ROLE_ADMIN", "ROLE_SUPERUSER"})
    public String employeeDashboard(Model model) {
        List<Employee> employees = employeeService.findAll();
        model.addAttribute("employees", employees);
        return "dashboard/employees";
    }

    @GetMapping("/employees/data")
    @ResponseBody
    public EmployeeDto getEmployeeData(@RequestParam("name") String name) {
        Employee employee = employeeService.findByName(name).orElse(null);
        if (employee != null) {
            return convertToDto(employee);
        }
        return new EmployeeDto(); // Return empty DTO if employee not found
    }

    @GetMapping({"/settings/employees/edit/{name}", "/settings/employees/edit"})
    @Secured({"ROLE_ADMIN", "ROLE_SUPERUSER"})
    public String editEmployee(@PathVariable(value = "name", required = false) String name,
                               @RequestParam(value = "name", required = false) String requestName,
                               Model model, @AuthenticationPrincipal UserDetails userDetails) {
        String employeeName = name != null ? name : requestName;

        if (employeeName != null) {
            Optional<Employee> employeeOptional = employeeService.findByName(employeeName);
            if (employeeOptional.isPresent()) {
                model.addAttribute("employeeDto", convertToDto(employeeOptional.get()));
            } else {
                model.addAttribute("employeeDto", new EmployeeDto()); // Provide an empty DTO if employee not found
            }
        } else {
            model.addAttribute("employeeDto", new EmployeeDto());
        }

        List<Employee> employees = employeeService.findAll();
        model.addAttribute("employees", employees);
        model.addAttribute("loggedInEmployee", userDetails); // Add logged-in employee details

        return "dashboard/settings/account";
    }

    @PostMapping("/save")
    @Secured({"ROLE_ADMIN", "ROLE_SUPERUSER"})
    public String saveEmployee(@ModelAttribute EmployeeDto employeeDto) {
        if (employeeDto.getEmployeeNumber() == null || employeeDto.getEmployeeNumber().isEmpty()) {
            String newEmployeeNumber = employeeService.generateUniqueEmployeeNumber();
            employeeDto.setEmployeeNumber(newEmployeeNumber);
        }
        employeeService.save(convertToEntity(employeeDto));
        return "redirect:/dashboard/employees";
    }

    private EmployeeDto convertToDto(Employee employee) {
        EmployeeDto dto = new EmployeeDto();
        dto.setName(employee.getName());
        dto.setLastName(employee.getLastName());
        dto.setEmail(employee.getEmail());
        dto.setEmployeeNumber(employee.getEmployeeNumber());
        dto.setPhone(employee.getPhoneNumber());
        dto.setAddress(employee.getAddress());
        dto.setAdmin(employee.isAdmin()); // Map admin field
        dto.setActive(employee.isActive()); // Map active field
        dto.setSuperUser(employee.isSuperUser()); // Map superuser field
        return dto;
    }

    private Employee convertToEntity(EmployeeDto dto) {
        Employee employee = new Employee();
        employee.setName(dto.getName());
        employee.setLastName(dto.getLastName());
        employee.setEmail(dto.getEmail());
        employee.setEmployeeNumber(dto.getEmployeeNumber());
        employee.setPhoneNumber(dto.getPhone());
        employee.setAddress(dto.getAddress());
        employee.setPassword(dto.getPassword()); // Hash password before saving
        employee.setAdmin(dto.isAdmin()); // Map admin field
        employee.setActive(dto.isActive()); // Map active field
        employee.setSuperUser(dto.isSuperUser()); // Map superuser field
        return employee;
    }
}
