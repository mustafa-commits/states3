//package com.ayn.states.realstate.controller;
//
//import com.ayn.states.realstate.dto.health.HealthSummaryDto;
//import com.ayn.states.realstate.dto.health.HttpResponseStatsDto;
//import com.ayn.states.realstate.dto.health.HttpTraceDto;
//import com.ayn.states.realstate.dto.health.SystemStatusDto;
//import com.ayn.states.realstate.service.health.HealthDashboardService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/dashboard")
//@CrossOrigin(origins = "*")
//public class HealthDashboardController {
//
//    @Autowired
//    private HealthDashboardService healthService;
//
//    @GetMapping("/system-status")
//    public ResponseEntity<SystemStatusDto> getSystemStatus() {
//        return ResponseEntity.ok(healthService.getSystemStatus());
//    }
//
//    @GetMapping("/http-responses")
//    public ResponseEntity<HttpResponseStatsDto> getHttpResponseStats() {
//        return ResponseEntity.ok(healthService.getHttpResponseStats());
//    }
//
//    @GetMapping("/traces")
//    public ResponseEntity<List<HttpTraceDto>> getHttpTraces() {
//        return ResponseEntity.ok(healthService.getHttpTraces());
//    }
//
//    @GetMapping("/health-summary")
//    public ResponseEntity<HealthSummaryDto> getHealthSummary() {
//        return ResponseEntity.ok(healthService.getHealthSummary());
//    }
//}
