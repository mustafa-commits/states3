package com.ayn.states.realstate.controller;

import java.util.List;

import com.ayn.states.realstate.entity.subscriptions.Subscription;
import com.ayn.states.realstate.entity.userSubscription.UserSubscription;
import com.ayn.states.realstate.repository.subscription.SubscriptionRepo;
import com.ayn.states.realstate.service.subscription.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private SubscriptionRepo subscriptionPlanRepository;

    @GetMapping("/plans")
    public ResponseEntity<List<Subscription>> getAvailablePlans() {
        List<Subscription> plans = subscriptionPlanRepository.findByActiveTrue();
        return ResponseEntity.ok(plans);
    }

    @PostMapping("/subscribe")
    public ResponseEntity<UserSubscription> subscribeUser(
            @RequestParam Long userId,
            @RequestParam Long planId) {
        try {
            UserSubscription subscription = subscriptionService.subscribeUser(userId, planId);
            return ResponseEntity.ok(subscription);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<UserSubscription> getUserSubscription(@PathVariable Long userId) {
        Optional<UserSubscription> subscription = subscriptionService.getActiveSubscription(userId);
        return subscription.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}/feature/{feature}")
    public ResponseEntity<Boolean> checkFeatureAccess(
            @PathVariable Long userId,
            @PathVariable String feature) {
        boolean hasAccess = subscriptionService.hasFeatureAccess(userId, feature);
        return ResponseEntity.ok(hasAccess);
    }

    @PostMapping("/cancel/{subscriptionId}")
    public ResponseEntity<UserSubscription> cancelSubscription(@PathVariable Long subscriptionId) {
        try {
            UserSubscription cancelled = subscriptionService.cancelSubscription(subscriptionId);
            return ResponseEntity.ok(cancelled);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/user/{userId}/increment-listing")
    public ResponseEntity<Void> incrementPropertyListing(@PathVariable Long userId) {
        if (subscriptionService.canCreatePropertyListing(userId)) {
            subscriptionService.incrementPropertyListingCount(userId);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }
}
