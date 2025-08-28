package com.ayn.states.realstate.dto.health;

public class HttpResponseStatsDto {
    private int successCount;    // 2xx responses
    private int notFoundCount;   // 404 responses
    private int badRequestCount; // 4xx responses
    private int serverErrorCount; // 5xx responses

    public HttpResponseStatsDto() {}

    public HttpResponseStatsDto(int successCount, int notFoundCount, int badRequestCount, int serverErrorCount) {
        this.successCount = successCount;
        this.notFoundCount = notFoundCount;
        this.badRequestCount = badRequestCount;
        this.serverErrorCount = serverErrorCount;
    }

    // getters and setters
    public int getSuccessCount() { return successCount; }
    public void setSuccessCount(int successCount) { this.successCount = successCount; }
    public int getNotFoundCount() { return notFoundCount; }
    public void setNotFoundCount(int notFoundCount) { this.notFoundCount = notFoundCount; }
    public int getBadRequestCount() { return badRequestCount; }
    public void setBadRequestCount(int badRequestCount) { this.badRequestCount = badRequestCount; }
    public int getServerErrorCount() { return serverErrorCount; }
    public void setServerErrorCount(int serverErrorCount) { this.serverErrorCount = serverErrorCount; }
}

