package com.ayn.states.realstate.dto.health;

public class HealthSummaryDto {
    private String applicationStatus;
    private SystemStatusDto systemStatus;
    private HttpResponseStatsDto responseStats;

    public HealthSummaryDto() {}

    public HealthSummaryDto(String applicationStatus, SystemStatusDto systemStatus, HttpResponseStatsDto responseStats) {
        this.applicationStatus = applicationStatus;
        this.systemStatus = systemStatus;
        this.responseStats = responseStats;
    }

    // getters and setters
    public String getApplicationStatus() { return applicationStatus; }
    public void setApplicationStatus(String applicationStatus) { this.applicationStatus = applicationStatus; }
    public SystemStatusDto getSystemStatus() { return systemStatus; }
    public void setSystemStatus(SystemStatusDto systemStatus) { this.systemStatus = systemStatus; }
    public HttpResponseStatsDto getResponseStats() { return responseStats; }
    public void setResponseStats(HttpResponseStatsDto responseStats) { this.responseStats = responseStats; }
}
