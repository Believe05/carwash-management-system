package com.carwash.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "loyalty_transactions")
public class LoyaltyTransaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    @Column(name = "points")
    private Integer points;
    
    @Column(name = "transaction_type")
    private String transactionType;
    
    @Column(name = "description", length = 500)
    private String description;
    
    @Column(name = "booking_id")
    private Long bookingId;
    
    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;
    
    // Constructor
    public LoyaltyTransaction() {}
    
    // Getters
    public Long getId() { 
        return id; 
    }
    
    public Customer getCustomer() { 
        return customer; 
    }
    
    public Integer getPoints() { 
        return points; 
    }
    
    public String getTransactionType() { 
        return transactionType; 
    }
    
    public String getDescription() { 
        return description; 
    }
    
    public Long getBookingId() { 
        return bookingId; 
    }
    
    public LocalDateTime getTransactionDate() { 
        return transactionDate; 
    }
    
    // Setters
    public void setId(Long id) { 
        this.id = id; 
    }
    
    public void setCustomer(Customer customer) { 
        this.customer = customer; 
    }
    
    public void setPoints(Integer points) { 
        this.points = points; 
    }
    
    public void setTransactionType(String transactionType) { 
        this.transactionType = transactionType; 
    }
    
    public void setDescription(String description) { 
        this.description = description; 
    }
    
    public void setBookingId(Long bookingId) { 
        this.bookingId = bookingId; 
    }
    
    public void setTransactionDate(LocalDateTime transactionDate) { 
        this.transactionDate = transactionDate; 
    }
    
    @PrePersist
    protected void onCreate() {
        if (transactionDate == null) {
            transactionDate = LocalDateTime.now();
        }
    }
}