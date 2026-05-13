package com.carwash.controller;

import com.carwash.entity.Booking;
import com.carwash.entity.Customer;
import com.carwash.entity.User;
import com.carwash.repository.CustomerRepository;
import com.carwash.service.BookingService;
import com.carwash.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
@RequestMapping("/client")
public class ClientController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private BookingService bookingService;
    
    private Customer getCurrentCustomer(Authentication authentication) {
        String email = authentication.getName();
        User user = userService.findByEmail(email);
        return customerRepository.findByUser(user)
            .orElseThrow(() -> new RuntimeException("Customer not found for email: " + email));
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        try {
            Customer customer = getCurrentCustomer(authentication);
            List<Booking> bookings = bookingService.getBookingsByCustomer(customer);
            
            long pendingCount = bookings.stream().filter(b -> "PENDING".equals(b.getStatus())).count();
            long approvedCount = bookings.stream().filter(b -> "APPROVED".equals(b.getStatus())).count();
            long completedCount = bookings.stream().filter(b -> "COMPLETED".equals(b.getStatus())).count();
            long paidCount = bookings.stream().filter(b -> "PAID".equals(b.getStatus())).count();
            
            // Get loyalty points safely
            Integer loyaltyPointsObj = customer.getLoyaltyPoints();
            int loyaltyPoints = loyaltyPointsObj != null ? loyaltyPointsObj : 0;
            
            String membershipTier = customer.getMembershipTier() != null ? customer.getMembershipTier() : "BRONZE";
            
            // Get next tier points
            int nextTierPoints = 0;
            if ("BRONZE".equals(membershipTier)) {
                nextTierPoints = 500 - loyaltyPoints;
                if (nextTierPoints < 0) nextTierPoints = 0;
            } else if ("SILVER".equals(membershipTier)) {
                nextTierPoints = 1000 - loyaltyPoints;
                if (nextTierPoints < 0) nextTierPoints = 0;
            }
            
            int discountPercent = membershipTier.equals("GOLD") ? 10 : (membershipTier.equals("SILVER") ? 5 : 0);
            
            Integer totalBookingsObj = customer.getTotalBookings();
            
            model.addAttribute("customer", customer);
            model.addAttribute("totalBookings", totalBookingsObj != null ? totalBookingsObj : 0);
            model.addAttribute("pendingCount", pendingCount);
            model.addAttribute("approvedCount", approvedCount);
            model.addAttribute("completedCount", completedCount);
            model.addAttribute("paidCount", paidCount);
            model.addAttribute("loyaltyPoints", loyaltyPoints);
            model.addAttribute("membershipTier", membershipTier);
            model.addAttribute("nextTierPoints", nextTierPoints);
            model.addAttribute("discountPercent", discountPercent);
            
            return "client/dashboard";
        } catch (Exception e) {
            return "redirect:/logout";
        }
    }
    
    @GetMapping("/book-wash")
    public String showBookWashForm() {
        return "client/book-wash";
    }
    
    @PostMapping("/book-wash")
    public String bookWash(@RequestParam String vehicleNumber,
                          @RequestParam String vehicleModel,
                          @RequestParam String serviceType,
                          Authentication authentication,
                          RedirectAttributes redirectAttributes) {
        try {
            Customer customer = getCurrentCustomer(authentication);
            bookingService.createBooking(customer, vehicleNumber, vehicleModel, serviceType);
            redirectAttributes.addFlashAttribute("success", "Booking created successfully!");
            return "redirect:/client/my-bookings";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create booking: " + e.getMessage());
            return "redirect:/client/book-wash";
        }
    }
    
    @GetMapping("/my-bookings")
    public String myBookings(Authentication authentication, Model model) {
        Customer customer = getCurrentCustomer(authentication);
        List<Booking> bookings = bookingService.getBookingsByCustomer(customer);
        model.addAttribute("bookings", bookings);
        return "client/my-bookings";
    }
    
    @GetMapping("/payments")
    public String payments(Authentication authentication, Model model) {
        Customer customer = getCurrentCustomer(authentication);
        List<Booking> allBookings = bookingService.getBookingsByCustomer(customer);
        
        // Split bookings into unpaid (COMPLETED) and paid (PAID)
        List<Booking> unpaidBookings = allBookings.stream()
            .filter(b -> "COMPLETED".equals(b.getStatus()))
            .toList();
        
        List<Booking> paidBookings = allBookings.stream()
            .filter(b -> "PAID".equals(b.getStatus()))
            .toList();
        
        model.addAttribute("unpaidBookings", unpaidBookings);
        model.addAttribute("paidBookings", paidBookings);
        
        return "client/payments";
    }
    
    @PostMapping("/make-payment")
    public String makePayment(@RequestParam Long bookingId,
                             @RequestParam String paymentMethod,
                             Authentication authentication,
                             RedirectAttributes redirectAttributes) {
        try {
            Customer customer = getCurrentCustomer(authentication);
            
            // Find the booking for this customer
            Booking booking = bookingService.getBookingsByCustomer(customer).stream()
                .filter(b -> b.getId().equals(bookingId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Booking not found for id: " + bookingId));
            
            // Check if booking is already paid
            if ("PAID".equals(booking.getStatus())) {
                throw new RuntimeException("Booking already paid!");
            }
            
            // Check if booking is completed
            if (!"COMPLETED".equals(booking.getStatus())) {
                throw new RuntimeException("Booking must be COMPLETED before payment. Current status: " + booking.getStatus());
            }
            
            // Process payment
            bookingService.makePayment(booking, paymentMethod);
            redirectAttributes.addFlashAttribute("success", "Payment completed successfully! Loyalty points have been added.");
            
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Payment failed: " + e.getMessage());
        }
        
        return "redirect:/client/payments";
    }
}