package com.jmis.JM_Store.repositories;

import com.jmis.JM_Store.enumerators.OrderStatus;
import com.jmis.JM_Store.enumerators.PaymentMethod;
import com.jmis.JM_Store.models.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Long> {
    // Find all orders by a specific customer
    List<Orders> findByCustomerId(Long customerId);

    // Find all orders with a specific status
    List<Orders> findByStatus(OrderStatus status);

    // Find all orders with a specific payment method
    List<Orders> findByPaymentMethod(PaymentMethod paymentMethod);

    // Custom query methods can be added as needed
}
