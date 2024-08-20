package com.jmis.JM_Store.repositories;

import com.jmis.JM_Store.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
