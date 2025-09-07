package com.ayn.states.realstate.controller;

import com.ayn.states.realstate.entity.tracking.CallClickTracking;
import com.ayn.states.realstate.service.token.TokenService;
import com.ayn.states.realstate.service.tracking.CallClickTrackingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for handling call button click tracking operations.
 * Simplified for mobile app usage.
 */
@RestController
@RequestMapping("/V1/api/tracking")
@RequiredArgsConstructor
@Slf4j
public class CallTrackingController {

    private final CallClickTrackingService callClickTrackingService;

    private final TokenService tokenService;

    /**
     * Record a call button click
     * POST /api/v1/call-tracking/record
     */
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> recordCallClick(
            @RequestBody CallClickRequest request,
            @RequestHeader(name = "Authorization") String token ) {

        try {
            Long userId = null;
            if (tokenService.decodeToken(token.substring(7)).getSubject().equals("0")){
                userId=tokenService.decodeToken(token.substring(7)).getClaim("UnRegistered");
            }else
                userId=Long.parseLong(tokenService.decodeToken(token.substring(7)).getSubject());

            CallClickTracking callClick = callClickTrackingService.recordCallClick(
                    request.compoundId(),
                    userId
            );

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Call click recorded successfully");
            response.put("clickId", callClick.getId());
            response.put("timestamp", callClick.getClickedAt());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error recording call click: compound={}",
                    request.compoundId(), e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to record call click");
            response.put("error", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Get call click statistics for a compound
     * GET /api/v1/call-tracking/stats/{compoundId}
     */
    @GetMapping("/statistics/{compoundId}")
    public ResponseEntity<Map<String, Object>> getCallClickStatistics(@PathVariable Long compoundId) {

        try {
            long totalCallClicks = callClickTrackingService.getCallClickCount(compoundId);
            long uniqueCallers = callClickTrackingService.getUniqueCallersCount(compoundId);

            Map<String, Object> response = new HashMap<>();
            response.put("compoundId", compoundId);
            response.put("totalCallClicks", totalCallClicks);
            response.put("uniqueCallers", uniqueCallers);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error retrieving call click statistics for compound: {}", compoundId, e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to retrieve call click statistics");
            response.put("error", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Get recent call clicks for a compound
     * GET /api/v1/call-tracking/recent/{compoundId}
     */
    @GetMapping("/recent/{compoundId}")
    public ResponseEntity<Map<String, Object>> getRecentCallClicks(
            @PathVariable Long compoundId,
            @RequestParam(defaultValue = "10") int limit) {

        try {
            List<CallClickTracking> recentCallClicks = callClickTrackingService.getRecentCallClicks(compoundId, limit);

            Map<String, Object> response = new HashMap<>();
            response.put("compoundId", compoundId);
            response.put("recentCallClicks", recentCallClicks);
            response.put("count", recentCallClicks.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error retrieving recent call clicks for compound: {}", compoundId, e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to retrieve recent call clicks");
            response.put("error", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Check if user has called today (for rate limiting)
     * GET /api/v1/call-tracking/check-today/{compoundId}
     */
    @GetMapping("/statistics-today/{compoundId}")
    public ResponseEntity<Map<String, Object>> checkUserCalledToday(
            @PathVariable Long compoundId,
            @RequestParam(required = false) String deviceId,
            Authentication authentication) {

        try {
            Long appUserId = null;

            if (authentication != null && authentication.isAuthenticated()) {
                // appUserId = extractUserIdFromAuthentication(authentication);
            }

            boolean hasCalledToday = callClickTrackingService.hasUserCalledToday(
                    compoundId, appUserId, deviceId);

            Map<String, Object> response = new HashMap<>();
            response.put("compoundId", compoundId);
            response.put("hasCalledToday", hasCalledToday);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error checking today's call clicks: compound={}", compoundId, e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to check today's call clicks");
            response.put("error", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Get call click trends for a compound
     * GET /api/v1/call-tracking/trends/{compoundId}
     */
    //@GetMapping("/trends/{compoundId}")
    public ResponseEntity<Map<String, Object>> getCallClickTrends(
            @PathVariable Long compoundId,
            @RequestParam(defaultValue = "30") int days) {

        try {
            LocalDateTime startDate = LocalDateTime.now().minusDays(days);
            LocalDateTime endDate = LocalDateTime.now();

            List<Object[]> trends = callClickTrackingService.getCallClickStatsByDateRange(compoundId, startDate, endDate);

            Map<String, Object> response = new HashMap<>();
            response.put("compoundId", compoundId);
            response.put("trends", trends);
            response.put("period", days + " days");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error retrieving call click trends for compound: {}", compoundId, e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to retrieve call click trends");
            response.put("error", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Request DTO for recording call clicks
     */

    public record CallClickRequest(
            Long compoundId
    ){}


}

