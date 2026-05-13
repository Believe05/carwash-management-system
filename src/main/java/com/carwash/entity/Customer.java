package com.carwash.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;
    
    @Column(name = "phone_number")
    private String phoneNumber;
    
    @Column(name = "address")
    private String address;
    
    @Column(name = "total_bookings")
    private Integer totalBookings = 0;
    
    @Column(name = "registered_at")
    private LocalDateTime registeredAt;
    
    // NEW FIELDS FOR LOYALTY PROGRAM
    @Column(name = "loyalty_points")
    private Integer loyaltyPoints = 0;
    
    @Column(name = "membership_tier")
    private String membershipTier = "BRONZE";
    
    @Column(name = "total_spent")
    private BigDecimal totalSpent = BigDecimal.ZERO;
    
    @Column(name = "last_booking_date")
    private LocalDateTime lastBookingDate;
    
    public Customer() {}
    
    public Customer(User user) {
        this.user = user;
        this.totalBookings = 0;
        this.loyaltyPoints = 0;
        this.membershipTier = "BRONZE";
        this.totalSpent = BigDecimal.ZERO;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public Integer getTotalBookings() { return totalBookings; }
    public void setTotalBookings(Integer totalBookings) { this.totalBookings = totalBookings; }
    
    public LocalDateTime getRegisteredAt() { return registeredAt; }
    public void setRegisteredAt(LocalDateTime registeredAt) { this.registeredAt = registeredAt; }
    
    public Integer getLoyaltyPoints() { return loyaltyPoints; }
    public void setLoyaltyPoints(Integer loyaltyPoints) { this.loyaltyPoints = loyaltyPoints; }
    
    public String getMembershipTier() { return membershipTier; }
    public void setMembershipTier(String membershipTier) { this.membershipTier = membershipTier; }
    
    public BigDecimal getTotalSpent() { return totalSpent; }
    public void setTotalSpent(BigDecimal totalSpent) { this.totalSpent = totalSpent; }
    
    public LocalDateTime getLastBookingDate() { return lastBookingDate; }
    public void setLastBookingDate(LocalDateTime lastBookingDate) { this.lastBookingDate = lastBookingDate; }
    
    @PrePersist
    protected void onCreate() {
        registeredAt = LocalDateTime.now();
        if (loyaltyPoints == null) loyaltyPoints = 0;
        if (membershipTier == null) membershipTier = "BRONZE";
        if (totalSpent == null) totalSpent = BigDecimal.ZERO;
    }
}