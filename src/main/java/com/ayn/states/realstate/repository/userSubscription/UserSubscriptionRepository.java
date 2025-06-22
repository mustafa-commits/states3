package com.ayn.states.realstate.repository.userSubscription;

import com.ayn.states.realstate.entity.userSubscription.UserSubscription;
import com.ayn.states.realstate.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long> {

    Optional<UserSubscription> findByUserIdAndStatus(Long userId, SubscriptionStatus status);

    List<UserSubscription> findByUserId(Long userId);

    @Query("SELECT us FROM UserSubscription us WHERE us.status = :status AND us.endDate < :currentDate")
    List<UserSubscription> findExpiredSubscriptions(SubscriptionStatus status, LocalDateTime currentDate);

    @Query("SELECT us FROM UserSubscription us WHERE us.autoRenew = true AND us.endDate BETWEEN :startDate AND :endDate")
    List<UserSubscription> findSubscriptionsForRenewal(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT us FROM UserSubscription us JOIN FETCH us.subscriptionPlan WHERE us.user.id = :userId AND us.status = 'ACTIVE'")
    Optional<UserSubscription> findActiveSubscriptionByUserId(Long userId);
}
