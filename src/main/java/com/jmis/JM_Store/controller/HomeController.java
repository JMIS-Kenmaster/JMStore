package com.jmis.JM_Store.controller;

import com.jmis.JM_Store.models.Product;
import com.jmis.JM_Store.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Set;

@Controller
public class HomeController {

    @Autowired
    private ProductService productService;

    @GetMapping({"", "/", "/home"})
    public String home(Model model) {
        // Fetch products from the service
        Set<Product> products = productService.findAll();
        // Add products to the model
        model.addAttribute("products", products);
        // Return the name of the Thymeleaf template
        return "home/index"; // Ensure this matches the Thymeleaf template file name
    }

    @GetMapping("/product_detail/{id}")
    public String productOverview(@PathVariable Long id, Model model) {
        // Fetch the specific product by ID from the service
        Product product = productService.findById(id);
        // Add the product to the model
        model.addAttribute("product", product);
        // Return the name of the Thymeleaf template
        return "home/product_detail"; // Ensure this matches the Thymeleaf template file name
    }

}
