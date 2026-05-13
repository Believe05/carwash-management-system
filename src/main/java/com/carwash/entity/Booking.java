package com.carwash.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    @Column(name = "vehicle_number", nullable = false)
    private String vehicleNumber;
    
    @Column(name = "vehicle_model")
    private String vehicleModel;
    
    @Column(name = "service_type", nullable = false)
    private String serviceType;
    
    @Column(name = "booking_date")
    private LocalDateTime bookingDate;
    
    @Column(name = "status")
    private String status = "PENDING";
    
    @Column(name = "total_amount")
    private BigDecimal totalAmount;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "payment_date")
private LocalDateTime paymentDate;

// Getter and Setter
public LocalDateTime getPaymentDate() { return paymentDate; }
public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }
    
    public Booking() {}
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    
    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }
    
    public String getVehicleModel() { return vehicleModel; }
    public void setVehicleModel(String vehicleModel) { this.vehicleModel = vehicleModel; }
    
    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }
    
    public LocalDateTime getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDateTime bookingDate) { this.bookingDate = bookingDate; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}