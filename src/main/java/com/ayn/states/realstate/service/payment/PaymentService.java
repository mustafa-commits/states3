package com.ayn.states.realstate.service.payment;

import com.ayn.states.realstate.entity.payment.Payment;
import com.ayn.states.realstate.enums.PaymentStatus;
import com.ayn.states.realstate.repository.payment.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    public Payment processPayment(Payment payment) {
        // This would integrate with actual payment gateway like Stripe, PayPal, etc.
        // For demo purposes, we'll simulate payment processing

        try {
            // Simulate payment gateway call
            boolean paymentSuccessful = simulatePaymentGateway(payment);

            if (paymentSuccessful) {
                payment.setStatus(PaymentStatus.COMPLETED);
                payment.setPaymentDate(LocalDateTime.now());
                payment.setTransactionId("TXN_" + System.currentTimeMillis());
                payment.setPaymentGateway("STRIPE");
            } else {
                payment.setStatus(PaymentStatus.FAILED);
            }

        } catch (Exception e) {
            payment.setStatus(PaymentStatus.FAILED);
        }

        return paymentRepository.save(payment);
    }

    private boolean simulatePaymentGateway(Payment payment) {
        // Simulate 95% success rate
        return Math.random() > 0.05;
    }

    public Payment refundPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new RuntimeException("Only completed payments can be refunded");
        }

        // Process refund with payment gateway
        payment.setStatus(PaymentStatus.REFUNDED);

        return paymentRepository.save(payment);
    }
}