package pl.inz.costshare.mobile.dto;

public class UserGroupDto {
    private Long id;
    private Long whichUserId;
    private Long whichGroupId;
    private Boolean ifAdmin;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getWhichUserId() {
        return whichUserId;
    }

    public void setWhichUserId(Long whichUserId) {
        this.whichUserId = whichUserId;
    }
    public Long getWhichGroupId() {
        return whichGroupId;
    }

    public void setWhichGroupId(Long whichGroupId) {
        this.whichGroupId = whichGroupId;
    }
}
