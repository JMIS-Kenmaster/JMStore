package com.jmis.JM_Store.services;

import com.jmis.JM_Store.models.Vendor;
import com.jmis.JM_Store.repositories.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class VendorService {
    @Autowired
    private VendorRepository vendorRepository;

    public Vendor save(Vendor vendor) {
        return vendorRepository.save(vendor);
    }

    public Set<Vendor> findAll() {
        return Set.copyOf(vendorRepository.findAll());
    }

    public Vendor findById(Integer id) {
        return vendorRepository.findById(id).orElse(null);
    }

    // Add more methods as needed
}
