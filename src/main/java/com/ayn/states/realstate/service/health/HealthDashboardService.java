//package com.ayn.states.realstate.service.health;
//
//import com.ayn.states.realstate.dto.health.HealthSummaryDto;
//import com.ayn.states.realstate.dto.health.HttpResponseStatsDto;
//import com.ayn.states.realstate.dto.health.HttpTraceDto;
//import com.ayn.states.realstate.dto.health.SystemStatusDto;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.actuate.health.HealthEndpoint;
//import org.springframework.boot.actuate.health.Status;
//import org.springframework.stereotype.Service;
//
//import javax.sql.DataSource;
//import java.io.File;
//import java.lang.management.ManagementFactory;
//import java.sql.Connection;
//import java.sql.DatabaseMetaData;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.ThreadLocalRandom;
//
//@Service
//public class HealthDashboardService {
//
//    @Autowired
//    private DataSource dataSource;
//
//    @Autowired(required = false)
//    private HealthEndpoint healthEndpoint;
//
//    private final List<HttpTraceDto> httpTraces = new ArrayList<>();
//    private long applicationStartTime = System.currentTimeMillis();
//
//    public SystemStatusDto getSystemStatus() {
//        SystemStatusDto systemStatus = new SystemStatusDto();
//
//        // Database status
//        systemStatus.setDatabase(getDatabaseStatus());
//
//        // Disk space
//        systemStatus.setDiskSpace(getDiskSpaceInfo());
//
//        // Processor info
//        systemStatus.setProcessor(getProcessorInfo());
//
//        // Uptime
//        systemStatus.setUptime(getUptime());
//
//        systemStatus.setLastUpdated(LocalDateTime.now());
//
//        return systemStatus;
//    }
//
//    private SystemStatusDto.DatabaseStatus getDatabaseStatus() {
//        try (Connection connection = dataSource.getConnection()) {
//            DatabaseMetaData metaData = connection.getMetaData();
//            String dbType = metaData.getDatabaseProductName();
//            String version = metaData.getDatabaseProductVersion();
//
//            return new SystemStatusDto.DatabaseStatus(
//                    dbType,
//                    version,
//                    connection.isValid(5) ? "UP" : "DOWN"
//            );
//        } catch (Exception e) {
//            return new SystemStatusDto.DatabaseStatus("Unknown", "Unknown", "DOWN");
//        }
//    }
//
//    private SystemStatusDto.DiskSpace getDiskSpaceInfo() {
//        File root = new File("/");
//        double totalSpace = root.getTotalSpace() / (1024.0 * 1024.0 * 1024.0); // Convert to GB
//        double freeSpace = root.getFreeSpace() / (1024.0 * 1024.0 * 1024.0);   // Convert to GB
//
//        return new SystemStatusDto.DiskSpace(totalSpace, freeSpace);
//    }
//
//    private SystemStatusDto.ProcessorInfo getProcessorInfo() {
//        int coreCount = Runtime.getRuntime().availableProcessors();
//        String processorName = System.getProperty("os.arch", "Unknown");
//
//        // Get CPU usage (simplified)
//        com.sun.management.OperatingSystemMXBean osBean =
//                (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
//        double cpuUsage = osBean.getProcessCpuLoad() * 100;
//
//        return new SystemStatusDto.ProcessorInfo(coreCount, processorName, cpuUsage);
//    }
//
//    private String getUptime() {
//        long uptime = System.currentTimeMillis() - applicationStartTime;
//        long hours = uptime / (1000 * 60 * 60);
//        long minutes = (uptime % (1000 * 60 * 60)) / (1000 * 60);
//        long seconds = (uptime % (1000 * 60)) / 1000;
//
//        return String.format("%02d:%02d:%02dh", hours, minutes, seconds);
//    }
//
//    public HttpResponseStatsDto getHttpResponseStats() {
//        // In real implementation, you would collect this from actual metrics
//        // For demo purposes, returning sample data
//        return new HttpResponseStatsDto(
//                ThreadLocalRandom.current().nextInt(20, 50),
//                ThreadLocalRandom.current().nextInt(3, 15),
//                ThreadLocalRandom.current().nextInt(1, 10),
//                ThreadLocalRandom.current().nextInt(5, 20)
//        );
//    }
//
//    public List<HttpTraceDto> getHttpTraces() {
//        // In real implementation, you would use Spring Boot Actuator's HttpTraceRepository
//        // or implement your own tracing mechanism
//        if (httpTraces.isEmpty()) {
//            generateSampleTraces();
//        }
//        return httpTraces;
//    }
//
//    private void generateSampleTraces() {
//        String[] methods = {"GET", "POST", "PUT", "DELETE"};
//        String[] uris = {
//                "/employees/find/4555",
//                "/employees/create",
//                "/employees/update/1234",
//                "/employees/delete/9876",
//                "/api/health",
//                "/api/users"
//        };
//        int[] statuses = {200, 404, 400, 500};
//
//        for (int i = 0; i < 10; i++) {
//            httpTraces.add(new HttpTraceDto(
//                    LocalDateTime.now().minusMinutes(i),
//                    methods[ThreadLocalRandom.current().nextInt(methods.length)],
//                    ThreadLocalRandom.current().nextLong(20, 200),
//                    statuses[ThreadLocalRandom.current().nextInt(statuses.length)],
//                    "http://localhost:8080" + uris[ThreadLocalRandom.current().nextInt(uris.length)]
//            ));
//        }
//    }
//
//    public HealthSummaryDto getHealthSummary() {
//        String appStatus = "UP";
//        if (healthEndpoint != null) {
//            try {
//                Status healthStatus = healthEndpoint.health().getStatus();
//                appStatus = healthStatus.getCode();
//            } catch (Exception e) {
//                appStatus = "DOWN";
//            }
//        }
//
//        return new HealthSummaryDto(
//                appStatus,
//                getSystemStatus(),
//                getHttpResponseStats()
//        );
//    }
//}