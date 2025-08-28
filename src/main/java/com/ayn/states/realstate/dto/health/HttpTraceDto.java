package com.ayn.states.realstate.dto.health;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class HttpTraceDto {
    @JsonFormat(pattern = "MMM dd, yyyy, hh:mm:ss a")
    private LocalDateTime timestamp;
    private String method;
    private long timeTaken;
    private int status;
    private String uri;

    public HttpTraceDto() {}

    public HttpTraceDto(LocalDateTime timestamp, String method, long timeTaken, int status, String uri) {
        this.timestamp = timestamp;
        this.method = method;
        this.timeTaken = timeTaken;
        this.status = status;
        this.uri = uri;
    }

    // getters and setters
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
    public long getTimeTaken() { return timeTaken; }
    public void setTimeTaken(long timeTaken) { this.timeTaken = timeTaken; }
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
    public String getUri() { return uri; }
    public void setUri(String uri) { this.uri = uri; }
}
