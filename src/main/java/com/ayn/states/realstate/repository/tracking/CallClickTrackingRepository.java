package com.ayn.states.realstate.repository.tracking;

import com.ayn.states.realstate.entity.tracking.CallClickTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for CallClickTracking entity operations.
 */
@Repository
public interface CallClickTrackingRepository extends JpaRepository<CallClickTracking, Long> {

    /**
     * Count total call clicks for a compound
     */
    long countByCompoundId(Long compoundId);

    /**
     * Find recent call clicks for a compound
     */
    @Query("SELECT cct FROM CallClickTracking cct WHERE cct.compoundId = :compoundId ORDER BY cct.clickedAt DESC LIMIT :limit")
    List<CallClickTracking> findRecentCallClicksByCompound(@Param("compoundId") Long compoundId, @Param("limit") int limit);

    /**
     * Count unique users who called a compound
     */
    @Query("SELECT COUNT(DISTINCT COALESCE(cct.appUserId, cct.unRegisteredId)) FROM CallClickTracking cct WHERE cct.compoundId = :compoundId")
    long countUniqueCallersByCompound(@Param("compoundId") Long compoundId);

    /**
     * Check if registered user called compound within date range
     */
    boolean existsByCompoundIdAndAppUserIdAndClickedAtBetween(
            Long compoundId,
            Long appUserId,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    /**
     * Check if device called compound within date range
     */
    boolean existsByCompoundIdAndUnRegisteredIdAndClickedAtBetween(
            Long compoundId,
            String unRegisteredId,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    /**
     * Get call click statistics by date range
     */
    @Query("SELECT DATE(cct.clickedAt) as callDate, COUNT(cct.id) as callCount " +
            "FROM CallClickTracking cct WHERE cct.compoundId = :compoundId " +
            "AND cct.clickedAt BETWEEN :startDate AND :endDate " +
            "GROUP BY DATE(cct.clickedAt) " +
            "ORDER BY callDate DESC")
    List<Object[]> getCallClickStatsByDateRange(
            @Param("compoundId") Long compoundId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Get most called phone numbers for a compound
     */
//    @Query("SELECT cct.phoneNumber, COUNT(cct.id) as callCount " +
//            "FROM CallClickTracking cct WHERE cct.compoundId = :compoundId " +
//            "AND cct.phoneNumber IS NOT NULL " +
//            "GROUP BY cct.phoneNumber " +
//            "ORDER BY callCount DESC " +
//            "LIMIT :limit")
//    List<Object[]> getMostCalledNumbers(@Param("compoundId") Long compoundId, @Param("limit") int limit);

    /**
     * Find call clicks by user within date range
     */
    @Query("SELECT cct FROM CallClickTracking cct WHERE cct.compoundId = :compoundId " +
            "AND (cct.appUserId = :appUserId OR cct.unRegisteredId = :unRegisteredId) " +
            "AND cct.clickedAt BETWEEN :startDate AND :endDate " +
            "ORDER BY cct.clickedAt DESC")
    List<CallClickTracking> findUserCallClicksInDateRange(
            @Param("compoundId") Long compoundId,
            @Param("appUserId") Long appUserId,
            @Param("unRegisteredId") String unRegisteredId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Get top compounds by call clicks
     */
    @Query("SELECT cct.compoundId, COUNT(cct.id) as callCount " +
            "FROM CallClickTracking cct WHERE cct.clickedAt >= :startDate " +
            "GROUP BY cct.compoundId " +
            "ORDER BY callCount DESC " +
            "LIMIT :limit")
    List<Object[]> getTopCompoundsByCallClicks(@Param("startDate") LocalDateTime startDate, @Param("limit") int limit);

    /**
     * Delete old call tracking records (for data cleanup)
     */
    @Query("DELETE FROM CallClickTracking cct WHERE cct.clickedAt < :cutoffDate")
    void deleteOldRecords(@Param("cutoffDate") LocalDateTime cutoffDate);
}

