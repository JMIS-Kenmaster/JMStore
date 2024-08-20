package com.jmis.JM_Store.services;

import com.jmis.JM_Store.models.Product;
import com.jmis.JM_Store.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public Set<Product> findAll() {
        return Set.copyOf(productRepository.findAll());
    }

    public Product findById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    // Add more methods as needed
}
