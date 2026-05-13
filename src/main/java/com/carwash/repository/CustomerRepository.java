// CustomerRepository.java
package com.carwash.repository;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.carwash.entity.Customer;
import com.carwash.entity.User;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByUser(User user);
    Optional<Customer> findByUserEmail(String email);
    // Add this method to CustomerRepository
@Query("SELECT SUM(c.totalSpent) FROM Customer c")
BigDecimal getTotalRevenue();

}