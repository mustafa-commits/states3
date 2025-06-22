package com.ayn.states.realstate.repository.subscription;

import com.ayn.states.realstate.entity.subscriptions.Subscription;
import com.ayn.states.realstate.enums.PlanType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepo extends JpaRepository<Subscription,Long> {
    List<Subscription> findByActiveTrue();

    Optional<Subscription> findByNameAndActiveTrue(String name);

    List<Subscription> findByPlanTypeAndActiveTrue(PlanType planType);
}
