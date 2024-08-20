package com.jmis.JM_Store.controller;

import com.jmis.JM_Store.models.Employee;
import com.jmis.JM_Store.services.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private EmployeeService employeeService;

    // Method to add the employee's name to the model before each request
    @ModelAttribute
    public void addEmployeeNameToModel(Model model, Authentication authentication) {
        if (authentication != null) {
            String employeeNumber = authentication.getName();
            Optional<Employee> employeeOptional = employeeService.findByEmployeeNumber(employeeNumber);
            if (employeeOptional.isPresent()) {
                Employee employee = employeeOptional.get();
                model.addAttribute("employeeName", employee.getName());
            } else {
                model.addAttribute("employeeName", "Unknown");
            }
        }
    }

    @GetMapping({"", "/", "/home"})
    public String dashboard(Model model) {
        model.addAttribute("content", "home");
        return "dashboard/home";
    }

    @GetMapping("/products")
    public String products(Model model) {
        model.addAttribute("content", "products");
        return "dashboard/home";
    }

    @GetMapping("/settings")
    public String settings(Model model) {
        model.addAttribute("content", "settings");
        return "dashboard/home";
    }
}
