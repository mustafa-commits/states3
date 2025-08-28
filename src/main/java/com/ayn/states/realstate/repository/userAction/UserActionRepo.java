package com.ayn.states.realstate.repository.userAction;

import com.ayn.states.realstate.entity.compound.Compound;
import com.ayn.states.realstate.entity.fav.UserActions;
import com.ayn.states.realstate.entity.states.States;
import com.ayn.states.realstate.entity.user.Users;
import com.ayn.states.realstate.enums.ActionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserActionRepo extends JpaRepository<UserActions, Long> {

    // Basic queries for view counting
//    long countByStateAndActionType(States state, ActionType actionType);

//    long countByCompoundAndActionType(Compound compound, ActionType actionType);

    // Find recent actions by user to prevent duplicate views
//    Optional<UserActions> findTopByUserAndStateAndActionTypeOrderByActionTimeDesc(
//            Users user, States state, ActionType actionType);
//
//    Optional<UserActions> findTopByUserAndCompoundAndActionTypeOrderByActionTimeDesc(
//            Users user, Compound compound, ActionType actionType);
//
//    // Find actions by session for unregistered users
//    Optional<UserActions> findTopBySessionIdAndStateAndActionTypeOrderByActionTimeDesc(
//            String sessionId, States state, ActionType actionType);
//
//    Optional<UserActions> findTopBySessionIdAndCompoundAndActionTypeOrderByActionTimeDesc(
//            String sessionId, Compound compound, ActionType actionType);

    // Analytics queries
    @Query("SELECT COUNT(DISTINCT COALESCE(ua.user.id, ua.unregisteredUser.tempIdentifier)) " +
            "FROM UserActions ua WHERE ua.state = :state AND ua.actionType = :actionType")
    long countUniqueViewersByState(@Param("state") States state, @Param("actionType") ActionType actionType);

    @Query("SELECT COUNT(DISTINCT COALESCE(ua.user.id, ua.unregisteredUser.tempIdentifier)) " +
            "FROM UserActions ua WHERE ua.compound = :compound AND ua.actionType = :actionType")
    long countUniqueViewersByCompound(@Param("compound") Compound compound, @Param("actionType") ActionType actionType);

    // Recent activity queries
    @Query("SELECT ua FROM UserActions ua WHERE ua.state = :state AND ua.actionType = :actionType " +
            "AND ua.actionTime >= :since ORDER BY ua.actionTime DESC")
    List<UserActions> findRecentStateActions(@Param("state") States state,
                                             @Param("actionType") ActionType actionType,
                                             @Param("since") LocalDateTime since);

    @Query("SELECT ua FROM UserActions ua WHERE ua.compound = :compound AND ua.actionType = :actionType " +
            "AND ua.actionTime >= :since ORDER BY ua.actionTime DESC")
    List<UserActions> findRecentCompoundActions(@Param("compound") Compound compound,
                                                @Param("actionType") ActionType actionType,
                                                @Param("since") LocalDateTime since);

    // Count views in time range
    @Query("SELECT COUNT(ua) FROM UserActions ua WHERE ua.state = :state AND ua.actionType = 'VIEW' " +
            "AND ua.actionTime BETWEEN :startDate AND :endDate")
    long countStateViewsInDateRange(@Param("state") States state,
                                    @Param("startDate") LocalDateTime startDate,
                                    @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(ua) FROM UserActions ua WHERE ua.compound = :compound AND ua.actionType = 'VIEW' " +
            "AND ua.actionTime BETWEEN :startDate AND :endDate")
    long countCompoundViewsInDateRange(@Param("compound") Compound compound,
                                       @Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);

    // Top viewed entities
    @Query("SELECT ua.state, COUNT(ua) as viewCount FROM UserActions ua " +
            "WHERE ua.actionType = 'VIEW' AND ua.state IS NOT NULL " +
            "GROUP BY ua.state ORDER BY viewCount DESC")
    List<Object[]> findTopViewedStates();

    @Query("SELECT ua.compound, COUNT(ua) as viewCount FROM UserActions ua " +
            "WHERE ua.actionType = 'VIEW' AND ua.compound IS NOT NULL " +
            "GROUP BY ua.compound ORDER BY viewCount DESC")
    List<Object[]> findTopViewedCompounds();

    // User activity queries
    List<UserActions> findByUserAndActionTypeOrderByActionTimeDesc(Users user, ActionType actionType);

    @Query("SELECT ua FROM UserActions ua WHERE ua.user = :user AND ua.actionType = :actionType " +
            "AND ua.actionTime >= :since ORDER BY ua.actionTime DESC")
    List<UserActions> findUserRecentActions(@Param("user") Users user,
                                            @Param("actionType") ActionType actionType,
                                            @Param("since") LocalDateTime since);

    // Bulk operations for analytics
    @Query("SELECT DATE(ua.actionTime) as date, COUNT(ua) as count " +
            "FROM UserActions ua WHERE ua.actionType = 'VIEW' " +
            "AND ua.actionTime >= :since GROUP BY DATE(ua.actionTime) ORDER BY date")
    List<Object[]> getDailyViewCounts(@Param("since") LocalDateTime since);

    @Query("SELECT ua.actionType, COUNT(ua) as count FROM UserActions ua " +
            "WHERE ua.actionTime >= :since GROUP BY ua.actionType")
    List<Object[]> getActionTypeCounts(@Param("since") LocalDateTime since);

    // IP-based duplicate detection
//    @Query("SELECT ua FROM UserActions ua WHERE ua.ipAddress = :ipAddress " +
//            "AND ua.state = :state AND ua.actionType = :actionType " +
//            "AND ua.actionTime >= :since ORDER BY ua.actionTime DESC")
//    List<UserActions> findRecentActionsByIpAndState(@Param("ipAddress") String ipAddress,
//                                                    @Param("state") States state,
//                                                    @Param("actionType") ActionType actionType,
//                                                    @Param("since") LocalDateTime since);
//
//    @Query("SELECT ua FROM UserActions ua WHERE ua.ipAddress = :ipAddress " +
//            "AND ua.compound = :compound AND ua.actionType = :actionType " +
//            "AND ua.actionTime >= :since ORDER BY ua.actionTime DESC")
//    List<UserActions> findRecentActionsByIpAndCompound(@Param("ipAddress") String ipAddress,
//                                                       @Param("compound") Compound compound,
//                                                       @Param("actionType") ActionType actionType,
//                                                       @Param("since") LocalDateTime since);
//
//    // Cleanup old records (for maintenance)
//    void deleteByActionTimeBefore(LocalDateTime cutoffDate);

    // Custom query for complex analytics
//    @Query(value = "SELECT " +
//            "entity_type, " +
//            "entity_id, " +
//            "total_views, " +
//            "unique_viewers, " +
//            "last_viewed_at, " +
//            "views_last_30_days " +
//            "FROM view_analytics " +
//            "ORDER BY total_views DESC " +
//            "LIMIT :limit", nativeQuery = true)
//    List<Object[]> getTopViewedEntities(@Param("limit") int limit);
}
