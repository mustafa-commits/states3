package com.ayn.states.realstate.repository.payment;

import com.ayn.states.realstate.entity.payment.Payment;
import com.ayn.states.realstate.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

//    List<Payment> findByUserId(Long userId);
//
//    List<Payment> findByUserIdAndStatus(Long userId, PaymentStatus status);
//
//    @Query("SELECT p FROM Payment p WHERE p.status = :status AND p.createdAt BETWEEN :startDate AND :endDate")
//    List<Payment> findPaymentsByStatusAndDateRange(PaymentStatus status, LocalDateTime startDate, LocalDateTime endDate);
}
