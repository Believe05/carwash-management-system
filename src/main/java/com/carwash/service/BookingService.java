package com.carwash.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.carwash.entity.Booking;
import com.carwash.entity.Customer;
import com.carwash.entity.Payment;
import com.carwash.repository.BookingRepository;
import com.carwash.repository.CustomerRepository;
import com.carwash.repository.PaymentRepository;

@Service
public class BookingService {
    
    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private LoyaltyService loyaltyService;
    
    private final Map<String, BigDecimal> servicePrices = new HashMap<>() {{
        put("Basic", new BigDecimal("500"));
        put("Premium", new BigDecimal("1000"));
        put("Deluxe", new BigDecimal("1500"));
    }};
    
    @Transactional
    public Booking createBooking(Customer customer, String vehicleNumber, String vehicleModel, String serviceType) {
        Booking booking = new Booking();
        booking.setCustomer(customer);
        booking.setVehicleNumber(vehicleNumber);
        booking.setVehicleModel(vehicleModel);
        booking.setServiceType(serviceType);
        booking.setBookingDate(LocalDateTime.now());
        booking.setStatus("PENDING");
        
        // Apply loyalty discount if applicable
        BigDecimal originalAmount = servicePrices.getOrDefault(serviceType, new BigDecimal("500"));
        BigDecimal discountedAmount = loyaltyService.calculateDiscountedAmount(originalAmount, customer);
        booking.setTotalAmount(discountedAmount);
        booking.setCreatedAt(LocalDateTime.now());
        
        Booking savedBooking = bookingRepository.save(booking);
        
        // Update customer total bookings
        Integer currentBookings = customer.getTotalBookings();
        customer.setTotalBookings((currentBookings != null ? currentBookings : 0) + 1);
        customerRepository.save(customer);
        
        return savedBooking;
    }
    
    @Transactional
    public Booking updateBookingStatus(Long bookingId, String status) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found with id: " + bookingId));
        booking.setStatus(status);
        
        // If status is COMPLETED, set completion date
        if ("COMPLETED".equals(status)) {
            booking.setBookingDate(LocalDateTime.now());
        }
        
        return bookingRepository.save(booking);
    }
    
    @Transactional
    public Payment makePayment(Booking booking, String paymentMethod) {
        // Check if booking is already paid
        if ("PAID".equals(booking.getStatus())) {
            throw new RuntimeException("Booking already paid!");
        }
        
        // Check if booking is completed (ready for payment)
        if (!"COMPLETED".equals(booking.getStatus())) {
            throw new RuntimeException("Booking must be COMPLETED before payment. Current status: " + booking.getStatus());
        }
        
        // Create payment record
        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(booking.getTotalAmount());
        payment.setPaymentMethod(paymentMethod);
        payment.setPaymentStatus("COMPLETED");
        payment.setTransactionId("TXN" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 5));
        payment.setPaymentDate(LocalDateTime.now());
        payment.setCreatedAt(LocalDateTime.now());
        
        // Update booking status to PAID and set payment date
        booking.setStatus("PAID");
        booking.setPaymentDate(LocalDateTime.now());
        bookingRepository.save(booking);
        
        // Add loyalty points for this purchase
        loyaltyService.addLoyaltyPoints(booking, booking.getCustomer());
        
        return paymentRepository.save(payment);
    }
    
    public List<Booking> getBookingsByCustomer(Customer customer) {
        return bookingRepository.findByCustomer(customer);
    }
    
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }
    
    public List<Booking> getPendingBookings() {
        return bookingRepository.findByStatus("PENDING");
    }
    
    public List<Booking> getApprovedBookings() {
        return bookingRepository.findByStatus("APPROVED");
    }
    
    public List<Booking> getCompletedBookings() {
        return bookingRepository.findByStatus("COMPLETED");
    }
    
    public List<Booking> getPaidBookings() {
        return bookingRepository.findByStatus("PAID");
    }
    
    public Map<String, Object> getDailyReport(LocalDateTime date) {
        LocalDateTime startOfDay = date.withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = date.withHour(23).withMinute(59).withSecond(59);
        
        List<Booking> bookings = bookingRepository.findBookingsBetweenDates(startOfDay, endOfDay);
        
        BigDecimal totalRevenue = bookings.stream()
            .filter(b -> "PAID".equals(b.getStatus()))
            .map(Booking::getTotalAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        Map<String, Object> report = new HashMap<>();
        report.put("date", date);
        report.put("totalBookings", bookings.size());
        report.put("pendingBookings", bookings.stream().filter(b -> "PENDING".equals(b.getStatus())).count());
        report.put("approvedBookings", bookings.stream().filter(b -> "APPROVED".equals(b.getStatus())).count());
        report.put("completedBookings", bookings.stream().filter(b -> "COMPLETED".equals(b.getStatus())).count());
        report.put("paidBookings", bookings.stream().filter(b -> "PAID".equals(b.getStatus())).count());
        report.put("totalRevenue", totalRevenue);
        report.put("bookings", bookings);
        
        return report;
    }
    
    public long countByStatus(String status) {
        return bookingRepository.countByStatus(status);
    }
    
    public BigDecimal getTotalRevenue() {
        return bookingRepository.findAll().stream()
            .filter(b -> "PAID".equals(b.getStatus()))
            .map(Booking::getTotalAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}