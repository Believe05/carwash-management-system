package com.carwash.repository;

import com.carwash.entity.Customer;
import com.carwash.entity.LoyaltyTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LoyaltyTransactionRepository extends JpaRepository<LoyaltyTransaction, Long> {
    
    List<LoyaltyTransaction> findByCustomerOrderByTransactionDateDesc(Customer customer);
    
    List<LoyaltyTransaction> findAllByOrderByTransactionDateDesc();
}