package com.carwash.service;

import com.carwash.entity.Booking;
import com.carwash.repository.BookingRepository;
import com.carwash.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {
    
    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    public Map<String, Object> getRevenueData(int days) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        List<Booking> bookings = bookingRepository.findBookingsBetweenDates(startDate, LocalDateTime.now());
        
        List<String> labels = new ArrayList<>();
        List<BigDecimal> values = new ArrayList<>();
        
        for (int i = days; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            labels.add(date.toString());
            
            BigDecimal dailyTotal = bookings.stream()
                .filter(b -> "COMPLETED".equals(b.getStatus()))
                .filter(b -> b.getCreatedAt().toLocalDate().equals(date))
                .map(Booking::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            values.add(dailyTotal);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("labels", labels);
        result.put("values", values);
        return result;
    }
    
    public List<Map<String, Object>> getTopCustomers() {
        return customerRepository.findAll().stream()
            .filter(c -> c.getTotalSpent() != null && c.getTotalSpent().compareTo(BigDecimal.ZERO) > 0)
            .sorted((c1, c2) -> c2.getTotalSpent().compareTo(c1.getTotalSpent()))
            .limit(5)
            .map(customer -> {
                Map<String, Object> map = new HashMap<>();
                map.put("name", customer.getUser().getName());
                map.put("totalSpent", customer.getTotalSpent());
                map.put("loyaltyPoints", customer.getLoyaltyPoints());
                map.put("membershipTier", customer.getMembershipTier());
                map.put("totalBookings", customer.getTotalBookings());
                return map;
            })
            .collect(Collectors.toList());
    }
    
    public Map<String, Object> getServicePopularity() {
        List<Booking> bookings = bookingRepository.findAll();
        
        Map<String, Long> serviceCount = bookings.stream()
            .collect(Collectors.groupingBy(Booking::getServiceType, Collectors.counting()));
        
        Map<String, Object> result = new HashMap<>();
        result.put("labels", new ArrayList<>(serviceCount.keySet()));
        result.put("values", new ArrayList<>(serviceCount.values()));
        return result;
    }
    
    public Map<String, Object> getDashboardAnalytics() {
        Map<String, Object> analytics = new HashMap<>();
        
        analytics.put("revenueLast7Days", getRevenueData(7));
        analytics.put("revenueLast30Days", getRevenueData(30));
        analytics.put("topCustomers", getTopCustomers());
        analytics.put("servicePopularity", getServicePopularity());
        
        // Calculate total revenue
        BigDecimal totalRevenue = bookingRepository.findAll().stream()
            .filter(b -> "COMPLETED".equals(b.getStatus()))
            .map(Booking::getTotalAmount)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        analytics.put("totalRevenue", totalRevenue);
        
        // Calculate average booking value
        OptionalDouble avg = bookingRepository.findAll().stream()
            .filter(b -> "COMPLETED".equals(b.getStatus()))
            .mapToDouble(b -> b.getTotalAmount() != null ? b.getTotalAmount().doubleValue() : 0)
            .average();
        analytics.put("averageBookingValue", avg.isPresent() ? avg.getAsDouble() : 0);
        
        return analytics;
    }
}