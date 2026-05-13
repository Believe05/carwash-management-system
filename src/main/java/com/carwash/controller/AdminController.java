package com.carwash.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.carwash.entity.Booking;
import com.carwash.entity.LoyaltyTransaction;
import com.carwash.repository.BookingRepository;
import com.carwash.repository.CustomerRepository;
import com.carwash.repository.LoyaltyTransactionRepository;
import com.carwash.repository.UserRepository;
import com.carwash.service.AnalyticsService;
import com.carwash.service.BookingService;
import com.carwash.service.LoyaltyService;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BookingService bookingService;
    
    @Autowired
    private LoyaltyService loyaltyService;
    
    @Autowired
    private AnalyticsService analyticsService;
    
    @Autowired
    private LoyaltyTransactionRepository loyaltyTransactionRepository;
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        long totalCustomers = customerRepository.count();
        long totalUsers = userRepository.count();
        long totalBookings = bookingRepository.count();
        long pendingBookings = bookingRepository.countByStatus("PENDING");
        long completedBookings = bookingRepository.countByStatus("COMPLETED");
        List<Booking> allBookings = bookingService.getAllBookings();
        
        model.addAttribute("totalCustomers", totalCustomers);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalBookings", totalBookings);
        model.addAttribute("pendingBookings", pendingBookings);
        model.addAttribute("completedBookings", completedBookings);
        model.addAttribute("bookings", allBookings);
        
        return "admin/dashboard";
    }
    
    @GetMapping("/manage-customers")
    public String manageCustomers(Model model) {
        model.addAttribute("customers", customerRepository.findAll());
        return "admin/manage-customers";
    }
    
    @GetMapping("/manage-bookings")
    public String manageBookings(Model model) {
        model.addAttribute("bookings", bookingService.getAllBookings());
        return "admin/manage-bookings";
    }
    
    @PostMapping("/approve-booking/{id}")
    public String approveBooking(@PathVariable Long id) {
        bookingService.updateBookingStatus(id, "APPROVED");
        return "redirect:/admin/manage-bookings";
    }
    
    @PostMapping("/reject-booking/{id}")
    public String rejectBooking(@PathVariable Long id) {
        bookingService.updateBookingStatus(id, "REJECTED");
        return "redirect:/admin/manage-bookings";
    }
    
    @PostMapping("/complete-booking/{id}")
    public String completeBooking(@PathVariable Long id) {
        bookingService.updateBookingStatus(id, "COMPLETED");
        return "redirect:/admin/manage-bookings";
    }
    
    @GetMapping("/daily-reports")
    public String dailyReports(@RequestParam(required = false) String date, Model model) {
        try {
            if (date == null || date.isEmpty()) {
                date = LocalDate.now().toString();
            }
            LocalDate selectedDate = LocalDate.parse(date);
            LocalDateTime dateTime = selectedDate.atStartOfDay();
            
            Map<String, Object> report = bookingService.getDailyReport(dateTime);
            model.addAttribute("report", report);
            model.addAttribute("selectedDate", date);
            
            return "admin/daily-reports";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error generating report: " + e.getMessage());
            return "admin/daily-reports";
        }
    }
    
    // NEW: Loyalty Dashboard
    @GetMapping("/loyalty")
    public String loyaltyDashboard(Model model) {
        model.addAttribute("loyaltyStats", loyaltyService.getLoyaltyStats());
        return "admin/loyalty-dashboard";
    }
    
    // NEW: Analytics Dashboard
    @GetMapping("/analytics")
    public String analyticsDashboard(Model model) {
        model.addAttribute("analytics", analyticsService.getDashboardAnalytics());
        return "admin/analytics-dashboard";
    }
    
    // NEW: API endpoint for loyalty transactions
    @GetMapping("/api/loyalty/transactions")
    @ResponseBody
    public List<Map<String, Object>> getLoyaltyTransactions() {
        List<LoyaltyTransaction> transactions = loyaltyTransactionRepository.findAllByOrderByTransactionDateDesc();
        return transactions.stream()
            .limit(50)
            .map(t -> {
                Map<String, Object> map = new java.util.HashMap<>();
                map.put("customerName", t.getCustomer().getUser().getName());
                map.put("points", t.getPoints());
                map.put("type", t.getTransactionType());
                map.put("description", t.getDescription());
                map.put("date", t.getTransactionDate().toString());
                return map;
            })
            .collect(Collectors.toList());
    }
}