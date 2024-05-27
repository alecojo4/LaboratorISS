package com.example.proiectiss.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@AttributeOverrides({
        @AttributeOverride(name="id", column=@Column(name="id"))
})
@Table(name="orders")
public class Order extends Entitate {
    @Column(name="registeredBy")
    private String username;
    @Column(name="status")
    private Status status;
    @Column(name="registeredAt")
    @Convert(converter = LocalDateTimeAttributeConverter.class)
    private LocalDateTime registeredAt;

    public Order(String username, Status status, LocalDateTime registeredAt) {
        this.username = username;
        this.status = status;
        this.registeredAt = registeredAt;
    }

    public Order() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }

    @Override
    public String toString() {
        return "Order{" +
                "username=" + username +
                ", status=" + status +
                ", registeredAt=" + registeredAt +
                '}';
    }
}
