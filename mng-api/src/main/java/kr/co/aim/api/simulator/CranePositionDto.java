package kr.co.aim.api.simulator;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class CranePositionDto {

    private String craneId;
    private double x;
    private double y;
    private String status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    public CranePositionDto() {
    }

    public CranePositionDto(String craneId, double x, double y, String status, LocalDateTime timestamp) {
        this.craneId = craneId;
        this.x = x;
        this.y = y;
        this.status = status;
        this.timestamp = timestamp;
    }

    public String getCraneId() {
        return craneId;
    }

    public void setCraneId(String craneId) {
        this.craneId = craneId;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "CranePositionDto{" +
                "craneId='" + craneId + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", status='" + status + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
