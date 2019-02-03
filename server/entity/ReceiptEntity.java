package pl.inz.costshare.server.entity;

import javax.persistence.*;

@Entity
@SequenceGenerator(initialValue = 100, name = "idgen", sequenceName = "receipt_sequence")
@Table(name = "receipt")
public class ReceiptEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idgen")
    @Column(updatable = false, nullable = false)
    private Long id;

    @Column(nullable = false)
    private String fileName;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private UserEntity uploadUser;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private EventEntity event;

    @Lob()
    @Basic(fetch = FetchType.LAZY)
    private byte[] rawData;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public UserEntity getUploadUser() {
        return uploadUser;
    }

    public void setUploadUser(UserEntity uploadUser) {
        this.uploadUser = uploadUser;
    }

    public byte[] getRawData() {
        return rawData;
    }

    public void setRawData(byte[] rawData) {
        this.rawData = rawData;
    }

    public EventEntity getEvent() {
        return event;
    }

    public void setEvent(EventEntity event) {
        this.event = event;
    }
}
