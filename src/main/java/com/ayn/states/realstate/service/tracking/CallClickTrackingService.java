package com.ayn.states.realstate.service.tracking;

import com.ayn.states.realstate.entity.tracking.CallClickTracking;
import com.ayn.states.realstate.repository.tracking.CallClickTrackingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for handling call button click tracking operations.
 * Simplified for mobile app usage.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CallClickTrackingService {

    private final CallClickTrackingRepository callClickTrackingRepository;

    @Transactional
    public CallClickTracking recordCallClick(Long compoundId,
                                             Long appUserId) {

        CallClickTracking callClick = new CallClickTracking();
        callClick.setCompoundId(compoundId);
        callClick.setAppUserId(appUserId);
        callClick.setClickedAt(LocalDateTime.now());

        CallClickTracking saved = callClickTrackingRepository.save(callClick);

        log.info("Recorded call click: compound={}, user={}",
                compoundId, appUserId != null ? appUserId : "anonymous");

        return saved;
    }


    /**
     * Get call click count for a specific compound
     */
    public long getCallClickCount(Long compoundId) {
        return callClickTrackingRepository.countByCompoundId(compoundId);
    }

    /**
     * Get recent call clicks for a compound
     */
    public List<CallClickTracking> getRecentCallClicks(Long compoundId, int limit) {
        return callClickTrackingRepository.findRecentCallClicksByCompound(compoundId, limit);
    }

    /**
     * Get unique callers count for a compound
     */
    public long getUniqueCallersCount(Long compoundId) {
        return callClickTrackingRepository.countUniqueCallersByCompound(compoundId);
    }

    /**
     * Check if a user has called this compound today (for rate limiting)
     */
    public boolean hasUserCalledToday(Long compoundId, Long appUserId, String deviceId) {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        if (appUserId != null) {
            return callClickTrackingRepository.existsByCompoundIdAndAppUserIdAndClickedAtBetween(
                    compoundId, appUserId, startOfDay, endOfDay);
        } else if (deviceId != null) {
            return callClickTrackingRepository.existsByCompoundIdAndUnRegisteredIdAndClickedAtBetween(
                    compoundId, deviceId, startOfDay, endOfDay);
        }

        return false;
    }

    /**
     * Get call click statistics for a date range
     */
    public List<Object[]> getCallClickStatsByDateRange(Long compoundId, LocalDateTime startDate, LocalDateTime endDate) {
        return callClickTrackingRepository.getCallClickStatsByDateRange(compoundId, startDate, endDate);
    }

    /**
     * Get most called phone numbers for a compound
     */
//    public List<Object[]> getMostCalledNumbers(Long compoundId, int limit) {
//        return callClickTrackingRepository.getMostCalledNumbers(compoundId, limit);
//    }
}
