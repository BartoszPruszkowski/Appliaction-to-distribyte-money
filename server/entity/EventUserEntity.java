package pl.inz.costshare.server.entity;


import javax.persistence.*;

@Entity
@SequenceGenerator(initialValue = 100, name = "idgen", sequenceName = "eventuser_sequence")
@Table(name = "EventUser")

public class EventUserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idgen")
    @Column(updatable = false, nullable = false)
    private Long id;

    @Column(nullable = false)
    private Boolean settled;

    @Column(nullable = false)
    private Double cost;

    @Column(nullable = false)
    private String userEventType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private EventEntity event;

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

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public EventEntity getEvent() {
        return event;
    }

    public void setEvent(EventEntity event) {
        this.event = event;
    }

    public Boolean getSettled() {
        return settled;
    }

    public void setSettled(Boolean settled) {
        this.settled = settled;
    }
}
