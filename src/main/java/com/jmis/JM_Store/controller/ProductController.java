package com.jmis.JM_Store.controller;

import com.jmis.JM_Store.models.Product;
import com.jmis.JM_Store.models.ProductDto;
import com.jmis.JM_Store.models.Vendor;
import com.jmis.JM_Store.repositories.ProductRepository;
import com.jmis.JM_Store.repositories.VendorRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/dashboard")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private VendorRepository vendorRepo;

    @GetMapping("/product_view")
    public String showProductList(Model model) {
        List<Product> products = productRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("products", products);
        return "dashboard/product_view";
    }

    @GetMapping({"/new_product", "/dashboard/new_product"})
    public String addProduct(Model model) {
        ProductDto productDto = new ProductDto();
        List<Vendor> vendorsList = vendorRepo.findAll(); // Fetch vendors from the database
        model.addAttribute("productDto", productDto);
        model.addAttribute("vendorsList", vendorsList); // Add vendors to the model
        return "dashboard/new_product";
    }

    @PostMapping({"/new_product", "/dashboard/new_product"})
    public String createProduct(
            @Valid @ModelAttribute ProductDto productDto,
            BindingResult result,
            RedirectAttributes redirectAttributes
    ) {
        if (productDto.getImageFile().isEmpty()) {
            result.addError(new FieldError("productDto", "imageFile", "The image is required"));
        }

        if (result.hasErrors()) {
            return "dashboard/new_product";
        }

        // Save the image method
        MultipartFile image = productDto.getImageFile();
        Date createdAt = new Date();
        String storageFileName = createdAt.getTime() + '_' + image.getOriginalFilename();

        try {
            String uploadDir = "public/images/";
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            try (InputStream inputStream = image.getInputStream()) {
                Files.copy(inputStream, Paths.get(uploadDir + storageFileName),
                        StandardCopyOption.REPLACE_EXISTING);
            }

        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
        }

        // Convert String vendor IDs to Integer and fetch Vendor entities
        Set<Vendor> selectedVendors = productDto.getVendors().stream()
                .map(Integer::valueOf) // Convert each String ID to Integer
                .map(vendorId -> vendorRepo.findById(vendorId).orElse(null)) // Fetch Vendor by ID
                .filter(Objects::nonNull) // Filter out nulls
                .collect(Collectors.toSet());

        Product product = new Product();
        product.setName(productDto.getName());
        product.setBrand(productDto.getBrand());
        product.setPrice(productDto.getPrice());
        product.setStock(productDto.getStock());
        product.setDescription(productDto.getDescription());
        product.setCreatedAt(new Date()); // Set the creation date to the current date
        product.setVendors(selectedVendors);
        product.setImageFileName(storageFileName);
        product.setImageUrls(productDto.getImageUrls());
        product.setTags(productDto.getTags());

        // Save the product entity using your repository or service
        productRepository.save(product);

        // Redirect to the product view page
        return "redirect:/dashboard/product_view";
    }
}
