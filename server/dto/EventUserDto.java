package pl.inz.costshare.server.dto;

public class EventUserDto {
    private Long id;
    private Long userId;
    private Long eventId;
    private Boolean settled;
    private Double cost;
    private String userEventType;

    public String getUserEventType() {
        return userEventType;
    }

    public void setUserEventType(String userEventType) {
        this.userEventType = userEventType;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getSettled() {
        return settled;
    }

    public void setSettled(Boolean settled) {
        this.settled = settled;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
