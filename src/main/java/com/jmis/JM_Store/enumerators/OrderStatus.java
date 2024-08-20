package com.jmis.JM_Store.enumerators;

public enum OrderStatus {
    PENDING,       // Order has been placed but not yet processed
    PROCESSING,    // Order is being processed (e.g., picked, packed)
    SHIPPED,       // Order has been shipped to the customer
    DELIVERED,     // Order has been delivered to the customer
    CANCELED       // Order has been canceled
}
