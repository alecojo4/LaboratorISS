package com.example.proiectiss.domain;

import jakarta.persistence.*;

@Entity
@AttributeOverrides({
        @AttributeOverride(name="id",column=@Column(name="id"))
})
@Table(name="order_items")
public class OrderItem extends Entitate {
    @ManyToOne
    @JoinColumn(name="drugId")
    private Drug drug;

    @Column(name="quantity")
    private Integer quantity;

    @Column(name="orderId")
    private Integer orderId;

    public OrderItem(Drug drug, Integer quantity, Integer orderId) {
        this.drug = drug;
        this.quantity = quantity;
        this.orderId = orderId;
    }

    public OrderItem() {
    }

    public Drug getDrug() {
        return drug;
    }

    public void setDrug(Drug drug) {
        this.drug = drug;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "drug=" + drug +
                ", quantity=" + quantity +
                ", orderId=" + orderId +
                '}';
    }
}
