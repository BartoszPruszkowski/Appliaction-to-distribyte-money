package pl.inz.costshare.server.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@SequenceGenerator(initialValue = 100, name = "idgen", sequenceName = "event_sequence")
@Table(name = "event")
public class EventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idgen")
    @Column(updatable = false, nullable = false)
    private Long id;

    @Column(updatable = true, nullable = false)
    private String name;

    @Column(updatable = true, nullable = false)
    private Boolean settled;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private GroupEntity group;

    @OneToMany(mappedBy = "event")
    private List<EventUserEntity> eventUserEntities  = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public Boolean getSettled() {
        return settled;
    }

    public void setSettled(Boolean settled) {
        this.settled = settled;
    }

    public List<EventUserEntity> getEventUserEntities() {
        return eventUserEntities;
    }

    public void setEventUserEntities(List<EventUserEntity> eventUserEntities) {
        this.eventUserEntities = eventUserEntities;
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

    public GroupEntity getGroup() {
        return group;
    }

    public void setGroup(GroupEntity group) {
        this.group = group;
    }
}
