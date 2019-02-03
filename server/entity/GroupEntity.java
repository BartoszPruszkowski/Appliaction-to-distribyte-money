package pl.inz.costshare.server.entity;


import javax.persistence.*;

@Entity
@SequenceGenerator(initialValue = 100, name = "idgen", sequenceName = "group_sequence")
@Table(name = "group_")
public class GroupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idgen")
    @Column(updatable = false, nullable = false)
    private Long id;

    @Column(nullable = false)
    private String groupName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
