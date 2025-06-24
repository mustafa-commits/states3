package com.ayn.states.realstate.service.subscription;

import com.ayn.states.realstate.entity.payment.Payment;
import com.ayn.states.realstate.entity.subscriptions.Subscription;
import com.ayn.states.realstate.entity.user.Users;
import com.ayn.states.realstate.entity.userSubscription.UserSubscription;
import com.ayn.states.realstate.enums.SubscriptionStatus;
import com.ayn.states.realstate.repository.payment.PaymentRepository;
import com.ayn.states.realstate.repository.subscription.SubscriptionRepo;
import com.ayn.states.realstate.repository.userSubscription.UserSubscriptionRepository;
import com.ayn.states.realstate.service.payment.PaymentService;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SubscriptionService {

    @Autowired
    private UserSubscriptionRepository userSubscriptionRepository;

    @Autowired
    private SubscriptionRepo subscriptionPlanRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentService paymentService;

    public UserSubscription subscribeUser(Long userId, Long planId) {
        Users user = new Users(); // This should come from UserService
        user.setUserId(userId);

        Optional<Subscription> planOpt = subscriptionPlanRepository.findById(planId);
        if (!planOpt.isPresent()) {
            throw new RuntimeException("Subscription plan not found");
        }

        Subscription plan = planOpt.get();

        // Check if user has active subscription
        Optional<UserSubscription> existingSubscription =
                userSubscriptionRepository.findByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE);

        if (existingSubscription.isPresent()) {
            throw new RuntimeException("User already has an active subscription");
        }

        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusMonths(plan.getDuration().getMonths());

//        UserSubscription subscription = new UserSubscription(user, plan, startDate, endDate);
//        UserSubscription savedSubscription = userSubscriptionRepository.save(subscription);

        // Create payment record
//        Payment payment = new Payment(user, savedSubscription, plan.getPrice());
//        payment.setDescription("Subscription payment for " + plan.getName());
//        paymentRepository.save(payment);
//
//        return savedSubscription;
        return null;
    }

    public Optional<UserSubscription> getActiveSubscription(Long userId) {
        return userSubscriptionRepository.findActiveSubscriptionByUserId(userId);
    }

    public boolean hasFeatureAccess(Long userId, String feature) {
        Optional<UserSubscription> subscription = getActiveSubscription(userId);
        if (!subscription.isPresent() || !subscription.get().getStatus().equals(SubscriptionStatus.ACTIVE)) {
            return false;
        }

//        SubscriptionPlan plan = subscription.get().getSubscriptionPlan();
//
//        switch (feature.toLowerCase()) {
//            case "featured_listings":
//                return plan.getFeaturedListings() != null && plan.getFeaturedListings();
//            case "priority_support":
//                return plan.getPrioritySupport() != null && plan.getPrioritySupport();
//            case "analytics":
//                return plan.getAnalyticsAccess() != null && plan.getAnalyticsAccess();
//            case "virtual_tour":
//                return plan.getVirtualTourAccess() != null && plan.getVirtualTourAccess();
//            case "lead_management":
//                return plan.getLeadManagement() != null && plan.getLeadManagement();
//            case "api_access":
//                return plan.getApiAccess() != null && plan.getApiAccess();
//            default:
//                return false;
//        }
        return false;
    }

//    public boolean canCreatePropertyListing(Long userId) {
//        Optional<UserSubscription> subscription = getActiveSubscription(userId);
//        if (!subscription.isPresent() || !subscription.get().getStatus().equals(SubscriptionStatus.ACTIVE)) {
//            return false;
//        }
//
//        return subscription.get().canCreateMoreListings();
//    }

    public void incrementPropertyListingCount(Long userId) {
        Optional<UserSubscription> subscription = getActiveSubscription(userId);
        if (subscription.isPresent()) {
            UserSubscription sub = subscription.get();
            sub.setCurrentPropertyListings(sub.getCurrentPropertyListings() + 1);
            sub.setTotalPropertyListings(sub.getTotalPropertyListings() + 1);
            userSubscriptionRepository.save(sub);
        }
    }

    public UserSubscription cancelSubscription(Long subscriptionId) {
        Optional<UserSubscription> subscriptionOpt = userSubscriptionRepository.findById(subscriptionId);
        if (!subscriptionOpt.isPresent()) {
            throw new RuntimeException("Subscription not found");
        }

        UserSubscription subscription = subscriptionOpt.get();
        subscription.setStatus(SubscriptionStatus.CANCELLED);
        subscription.setAutoRenew(false);

        return userSubscriptionRepository.save(subscription);
    }

    public List<UserSubscription> getSubscriptionsForRenewal() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endDate = now.plusDays(7); // Check for subscriptions expiring in next 7 days

        return userSubscriptionRepository.findSubscriptionsForRenewal(now, endDate);
    }

    public void processExpiredSubscriptions() {
        List<UserSubscription> expiredSubscriptions =
                userSubscriptionRepository.findExpiredSubscriptions(SubscriptionStatus.ACTIVE, LocalDateTime.now());

        for (UserSubscription subscription : expiredSubscriptions) {
            subscription.setStatus(SubscriptionStatus.EXPIRED);
            userSubscriptionRepository.save(subscription);
        }
    }
}