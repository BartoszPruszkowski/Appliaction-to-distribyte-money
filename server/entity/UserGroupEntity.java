package pl.inz.costshare.server.entity;

import javax.persistence.*;


@Entity
@SequenceGenerator(initialValue = 100, name = "idgen", sequenceName = "usergroup_sequence")
@Table(name = "UserGroup")
public class UserGroupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idgen")
    @Column(updatable = false, nullable = false)
    private Long id;

    @Column(nullable = false)
    private Boolean admin;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private GroupEntity group;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public GroupEntity getGroup() {
        return group;
    }

    public void setGroup(GroupEntity group) {
        this.group = group;
    }
}
