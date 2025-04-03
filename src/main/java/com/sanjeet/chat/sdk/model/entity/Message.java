package com.sanjeet.chat.sdk.model.entity;

import jakarta.persistence.*;

import java.util.Date;


@Entity(name = "Message")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "message", nullable = false)
    private String message;
    @Column(name = "receiverPhone", nullable = false)
    private String receiverPhone;
    @Column(name = "senderPhone", nullable = false)
    private String senderPhone;
    @Column(name = "apiKey", nullable = false)
    private String apiKey;
    @Column(name = "createdAt", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    public Message() {
    }

    public Message(long id, String message, String apiKey, String receiverPhone) {
        this.id = id;
        this.message = message;
        this.apiKey = apiKey;
        this.receiverPhone = receiverPhone;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date(); // Set current timestamp before saving
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getSenderPhone() {
        return senderPhone;
    }

    public void setSenderPhone(String senderPhone) {
        this.senderPhone = senderPhone;
    }


    @Override
    public String toString() {
        return "MessageDetailsEntity{" +
                "id=" + id +
                ", message='" + message + '\'' +
                ", apiKey='" + apiKey + '\'' +
                ", receiverPhone='" + receiverPhone + '\'' +
                ", senderPhone='" + senderPhone + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
