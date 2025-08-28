package com.ayn.states.realstate.controller;

import com.ayn.states.realstate.SecuredRestController;
import com.ayn.states.realstate.service.userAction.ViewTrackingService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class ViewTrackingController implements SecuredRestController {

    @Autowired
    private ViewTrackingService viewTrackingService;


    @GetMapping("/V1/api/compounds/{compoundId}/view")
    public boolean addCompoundView(@PathVariable Long compoundId,
            @RequestHeader("Authorization") String token) {

        return viewTrackingService.addCompoundView(compoundId,token);
    }

    /**
     * Add a view to a state
     * POST /api/states/{stateId}/views
     * Headers: Authorization: Bearer {token}
     */
    @GetMapping("/V1/api/states/{stateId}/view")
    public boolean addStateView(@PathVariable Long stateId,
                                                            @RequestHeader("Authorization") String token) {

        return viewTrackingService.addStateView(stateId,token);
    }


    /**
     * Get compound analytics (views, unique views, favorites, etc.)
     * GET /api/compounds/{compoundId}/analytics
     */
//    @GetMapping("/V1/api/compounds/{compoundId}/analytics")
//    public ResponseEntity<Map<String, Object>> getCompoundAnalytics(@PathVariable Long compoundId) {
//        Map<String, Object> response = new HashMap<>();
//
//        ViewTrackingService.CompoundAnalytics analytics = viewTrackingService.getCompoundAnalytics(compoundId);
//
//        if (analytics != null) {
//            response.put("success", true);
//            response.put("compoundId", compoundId);
//            response.put("totalViews", analytics.totalViews);
//            response.put("uniqueViews", analytics.uniqueViews);
//            response.put("favorites", analytics.favorites);
//            response.put("lastViewedAt", analytics.lastViewedAt);
//            response.put("engagementScore", analytics.engagementScore);
//            return ResponseEntity.ok(response);
//        } else {
//            response.put("success", false);
//            response.put("message", "Compound not found");
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//        }
//    }

    /**
     * Get state analytics (views, unique views, favorites, etc.)
     * GET /api/states/{stateId}/analytics
     */
//    @GetMapping("/V1/api/states/{stateId}/analytics")
//    public ResponseEntity<Map<String, Object>> getStateAnalytics(@PathVariable Long stateId) {
//        Map<String, Object> response = new HashMap<>();
//
//        ViewTrackingService.StateAnalytics analytics = viewTrackingService.getStateAnalytics(stateId);
//
//        if (analytics != null) {
//            response.put("success", true);
//            response.put("stateId", stateId);
//            response.put("totalViews", analytics.totalViews);
//            response.put("uniqueViews", analytics.uniqueViews);
//            response.put("favorites", analytics.favorites);
//            response.put("recentViews", analytics.recentViews);
//            response.put("lastViewedAt", analytics.lastViewedAt);
//            response.put("engagementScore", analytics.engagementScore);
//            response.put("popularityTrend", analytics.popularityTrend);
//            return ResponseEntity.ok(response);
//        } else {
//            response.put("success", false);
//            response.put("message", "State not found");
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//        }
//    }

    /**
     * Get compound view count (simple endpoint for backward compatibility)
     * GET /api/compounds/{compoundId}/views
     */
//    @GetMapping("/V1/api/compounds/{compoundId}/views")
    public ResponseEntity<Map<String, Object>> getCompoundViewCount(@PathVariable Long compoundId) {
        Map<String, Object> response = new HashMap<>();

        int viewCount = viewTrackingService.getCompoundViewCount(compoundId);

        if (viewCount >= 0) {
            response.put("success", true);
            response.put("compoundId", compoundId);
            response.put("viewCount", viewCount);
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Compound not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Get state view count (simple endpoint for backward compatibility)
     * GET /api/states/{stateId}/views
     */
//    @GetMapping("/V1/api/states/{stateId}/views")
    public ResponseEntity<Map<String, Object>> getStateViewCount(@PathVariable Long stateId) {
        Map<String, Object> response = new HashMap<>();

        int viewCount = viewTrackingService.getStateViewCount(stateId);

        if (viewCount >= 0) {
            response.put("success", true);
            response.put("stateId", stateId);
            response.put("viewCount", viewCount);
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "State not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

}
