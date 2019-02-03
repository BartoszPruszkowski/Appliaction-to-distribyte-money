package pl.inz.costshare.server.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude
public class EventDto {
    private Long id;
    private String name;
    private Long groupId;
    private Boolean settled;
    private List<EventUserDto> eventUsers;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public List<EventUserDto> getEventUsers() {
        return eventUsers;
    }

    public void setEventUsers(List<EventUserDto> eventUsers) {
        this.eventUsers = eventUsers;
    }

    public Boolean getSettled() {
        return settled;
    }

    public void setSettled(Boolean settled) {
        this.settled = settled;
    }
}
