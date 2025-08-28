package com.ayn.states.realstate.dto.health;


import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class SystemStatusDto {
    private DatabaseStatus database;
    private DiskSpace diskSpace;
    private ProcessorInfo processor;
    private String uptime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastUpdated;

    // Constructors, getters, setters
    public SystemStatusDto() {}

    public static class DatabaseStatus {
        private String type;
        private String version;
        private String status;

        public DatabaseStatus() {}

        public DatabaseStatus(String type, String version, String status) {
            this.type = type;
            this.version = version;
            this.status = status;
        }

        // getters and setters
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    public static class DiskSpace {
        private double totalSpace;
        private double freeSpace;
        private double usedSpace;
        private int usagePercentage;

        public DiskSpace() {}

        public DiskSpace(double totalSpace, double freeSpace) {
            this.totalSpace = totalSpace;
            this.freeSpace = freeSpace;
            this.usedSpace = totalSpace - freeSpace;
            this.usagePercentage = (int) ((usedSpace / totalSpace) * 100);
        }

        // getters and setters
        public double getTotalSpace() { return totalSpace; }
        public void setTotalSpace(double totalSpace) { this.totalSpace = totalSpace; }
        public double getFreeSpace() { return freeSpace; }
        public void setFreeSpace(double freeSpace) { this.freeSpace = freeSpace; }
        public double getUsedSpace() { return usedSpace; }
        public void setUsedSpace(double usedSpace) { this.usedSpace = usedSpace; }
        public int getUsagePercentage() { return usagePercentage; }
        public void setUsagePercentage(int usagePercentage) { this.usagePercentage = usagePercentage; }
    }

    public static class ProcessorInfo {
        private int coreCount;
        private String processorName;
        private double cpuUsage;

        public ProcessorInfo() {}

        public ProcessorInfo(int coreCount, String processorName, double cpuUsage) {
            this.coreCount = coreCount;
            this.processorName = processorName;
            this.cpuUsage = cpuUsage;
        }

        // getters and setters
        public int getCoreCount() { return coreCount; }
        public void setCoreCount(int coreCount) { this.coreCount = coreCount; }
        public String getProcessorName() { return processorName; }
        public void setProcessorName(String processorName) { this.processorName = processorName; }
        public double getCpuUsage() { return cpuUsage; }
        public void setCpuUsage(double cpuUsage) { this.cpuUsage = cpuUsage; }
    }

    // Main class getters and setters
    public DatabaseStatus getDatabase() { return database; }
    public void setDatabase(DatabaseStatus database) { this.database = database; }
    public DiskSpace getDiskSpace() { return diskSpace; }
    public void setDiskSpace(DiskSpace diskSpace) { this.diskSpace = diskSpace; }
    public ProcessorInfo getProcessor() { return processor; }
    public void setProcessor(ProcessorInfo processor) { this.processor = processor; }
    public String getUptime() { return uptime; }
    public void setUptime(String uptime) { this.uptime = uptime; }
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
}
