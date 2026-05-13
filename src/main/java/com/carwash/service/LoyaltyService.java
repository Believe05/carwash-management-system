package com.carwash.service;

import com.carwash.entity.Booking;
import com.carwash.entity.Customer;
import com.carwash.entity.LoyaltyTransaction;
import com.carwash.repository.CustomerRepository;
import com.carwash.repository.LoyaltyTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LoyaltyService {
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private LoyaltyTransactionRepository loyaltyTransactionRepository;
    
    private static final int POINTS_PER_TEN_RAND = 10;
    private static final int SILVER_THRESHOLD = 500;
    private static final int GOLD_THRESHOLD = 1000;
    
    @Transactional
    public void addLoyaltyPoints(Booking booking, Customer customer) {
        if (customer == null || booking == null) return;
        
        int pointsEarned = calculatePoints(booking.getTotalAmount());
        
        // Update customer
        customer.setLoyaltyPoints(customer.getLoyaltyPoints() + pointsEarned);
        customer.setTotalSpent(customer.getTotalSpent().add(booking.getTotalAmount()));
        customer.setLastBookingDate(LocalDateTime.now());
        
        // Update tier based on total spent
        updateMembershipTier(customer);
        
        customerRepository.save(customer);
        
        // Record transaction
        LoyaltyTransaction transaction = new LoyaltyTransaction();
        transaction.setCustomer(customer);
        transaction.setPoints(pointsEarned);
        transaction.setTransactionType("EARNED");
        transaction.setDescription("Points earned for booking #" + booking.getId() + " - " + booking.getServiceType());
        transaction.setBookingId(booking.getId());
        loyaltyTransactionRepository.save(transaction);
    }
    
    private int calculatePoints(BigDecimal amount) {
        if (amount == null) return 0;
        return (amount.intValue() / 10) * POINTS_PER_TEN_RAND;
    }
    
    private void updateMembershipTier(Customer customer) {
        int totalSpent = customer.getTotalSpent().intValue();
        String oldTier = customer.getMembershipTier();
        String newTier = oldTier;
        
        if (totalSpent >= GOLD_THRESHOLD) {
            newTier = "GOLD";
        } else if (totalSpent >= SILVER_THRESHOLD) {
            newTier = "SILVER";
        } else {
            newTier = "BRONZE";
        }
        
        if (!newTier.equals(oldTier)) {
            customer.setMembershipTier(newTier);
            
            // Record tier upgrade
            LoyaltyTransaction transaction = new LoyaltyTransaction();
            transaction.setCustomer(customer);
            transaction.setPoints(0);
            transaction.setTransactionType("TIER_UPGRADE");
            transaction.setDescription("Upgraded from " + oldTier + " to " + newTier + " tier!");
            loyaltyTransactionRepository.save(transaction);
        }
    }
    
    public int getDiscountPercentage(Customer customer) {
        if (customer == null) return 0;
        switch(customer.getMembershipTier()) {
            case "GOLD": return 10;
            case "SILVER": return 5;
            default: return 0;
        }
    }
    
    public BigDecimal calculateDiscountedAmount(BigDecimal originalAmount, Customer customer) {
        if (originalAmount == null || customer == null) return originalAmount;
        int discountPercent = getDiscountPercentage(customer);
        BigDecimal discount = originalAmount.multiply(BigDecimal.valueOf(discountPercent))
                .divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
        return originalAmount.subtract(discount);
    }
    
    public List<LoyaltyTransaction> getAllTransactions() {
        return loyaltyTransactionRepository.findAllByOrderByTransactionDateDesc();
    }
    
    public Map<String, Object> getLoyaltyStats() {
        Map<String, Object> stats = new HashMap<>();
        
        List<Customer> allCustomers = customerRepository.findAll();
        
        long totalPoints = allCustomers.stream()
                .mapToInt(c -> c.getLoyaltyPoints() != null ? c.getLoyaltyPoints() : 0)
                .sum();
        
        long goldCount = allCustomers.stream()
                .filter(c -> "GOLD".equals(c.getMembershipTier()))
                .count();
        
        long silverCount = allCustomers.stream()
                .filter(c -> "SILVER".equals(c.getMembershipTier()))
                .count();
        
        long bronzeCount = allCustomers.stream()
                .filter(c -> "BRONZE".equals(c.getMembershipTier()))
                .count();
        
        stats.put("totalPointsEarned", totalPoints);
        stats.put("goldMembers", goldCount);
        stats.put("silverMembers", silverCount);
        stats.put("bronzeMembers", bronzeCount);
        
        return stats;
    }
}