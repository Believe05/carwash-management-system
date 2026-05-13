// BookingRepository.java
package com.carwash.repository;

import com.carwash.entity.Booking;
import com.carwash.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByCustomer(Customer customer);
    List<Booking> findByStatus(String status);
    
    @Query("SELECT b FROM Booking b WHERE b.createdAt BETWEEN :startDate AND :endDate")
    List<Booking> findBookingsBetweenDates(@Param("startDate") LocalDateTime startDate, 
                                           @Param("endDate") LocalDateTime endDate);
    
    long countByStatus(String status);
}