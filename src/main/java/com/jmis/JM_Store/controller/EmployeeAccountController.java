package com.jmis.JM_Store.controller;

import com.jmis.JM_Store.models.Employee;
import com.jmis.JM_Store.models.EmployeeDto;
import com.jmis.JM_Store.repositories.EmployeeRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Date;
import java.util.Optional;

@Controller
public class EmployeeAccountController {

    @Autowired
    private EmployeeRepository repo;

    @GetMapping("/register")
    public String register(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<Employee> loggedInEmployeeOptional = repo.findByEmployeeNumber(auth.getName());

        if (loggedInEmployeeOptional.isPresent() && (loggedInEmployeeOptional.get().isAdmin() || loggedInEmployeeOptional.get().isSuperUser())) {
            EmployeeDto employeeDto = new EmployeeDto();
            model.addAttribute("employeeDto", employeeDto);
            model.addAttribute("success", false);
            return "register";
        } else {
            return "redirect:/dashboard?error=unauthorized";
        }
    }

    @PostMapping("/register")
    public String register(Model model,
                           @Valid @ModelAttribute EmployeeDto employeeDto,
                           BindingResult result) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<Employee> loggedInEmployeeOptional = repo.findByEmployeeNumber(auth.getName());

        if (loggedInEmployeeOptional.isEmpty() || !(loggedInEmployeeOptional.get().isAdmin() || loggedInEmployeeOptional.get().isSuperUser())) {
            return "redirect:/dashboard?error=unauthorized";
        }

        if (!employeeDto.getPassword().equals(employeeDto.getConfirmPassword())) {
            result.addError(
                    new FieldError("employeeDto", "confirmPassword", "Passwords do not match")
            );
        }

        Optional<Employee> existingEmployeeOptional = repo.findByEmployeeNumber(employeeDto.getEmployeeNumber());
        if (existingEmployeeOptional.isPresent()) {
            result.addError(
                    new FieldError("employeeDto", "employeeNumber", "This employee number is already in the database")
            );
        }

        if (result.hasErrors()) {
            return "register";
        }

        try {
            var bCryptEncoder = new BCryptPasswordEncoder();
            // Creating a new employee
            Employee newEmployee = new Employee();
            newEmployee.setName(employeeDto.getName());
            newEmployee.setLastName(employeeDto.getLastName());
            newEmployee.setEmail(employeeDto.getEmail());
            newEmployee.setEmployeeNumber(employeeDto.getEmployeeNumber());
            newEmployee.setPhoneNumber(employeeDto.getPhone());
            newEmployee.setAddress(employeeDto.getAddress());
            newEmployee.setPassword(bCryptEncoder.encode(employeeDto.getPassword()));
            newEmployee.setRole("USER"); // Set default role
            newEmployee.setActive(true);
            newEmployee.setAdmin(false);
            newEmployee.setSuperUser(false);
            newEmployee.setCreatedAt(new Date());

            repo.save(newEmployee);

            model.addAttribute("employeeDto", new EmployeeDto());
            model.addAttribute("success", true);
        } catch (Exception ex) {
            result.addError(
                    new FieldError("employeeDto", "name", ex.getMessage())
            );
        }

        return "register";
    }
}

