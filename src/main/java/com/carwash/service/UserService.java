// UserService.java
package com.carwash.service;

import com.carwash.dto.UserRegistrationDto;
import com.carwash.entity.User;
import com.carwash.entity.Customer;
import com.carwash.repository.UserRepository;
import com.carwash.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Transactional
    public User registerUser(UserRegistrationDto registrationDto) {
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new RuntimeException("Email already exists!");
        }
        
        User user = new User();
        user.setName(registrationDto.getName());
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setRole("ROLE_" + registrationDto.getRole().toUpperCase());
        
        User savedUser = userRepository.save(user);
        
        // If role is CLIENT, create customer record
        if ("ROLE_CLIENT".equals(savedUser.getRole())) {
            Customer customer = new Customer();
            customer.setUser(savedUser);
            customer.setTotalBookings(0);
            customerRepository.save(customer);
        }
        
        return savedUser;
    }
    
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }
}