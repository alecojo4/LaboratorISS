package com.example.proiectiss.domain;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@AttributeOverrides({
        @AttributeOverride(name="id", column=@Column(name="id"))
})
@Table(name="drugs")
public class Drug extends Entitate {
    @Column(name="name")
    private String name;
    @Column(name="description")
    private String description;
    @Column(name="dateProduced")
    @Convert(converter = LocalDateStringConverter.class)
    private LocalDate dateProduced;

    public Drug(String name, String description, LocalDate dateProduced) {
        this.name = name;
        this.description = description;
        this.dateProduced = dateProduced;
    }

    public Drug() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDateProduced() {
        return dateProduced;
    }

    public void setDateProduced(LocalDate dateProduced) {
        this.dateProduced = dateProduced;
    }

    @Override
    public String toString() {
        return "Drug{" +
                "name='" + name + '\'' +
                ", description=" + description +
                ", dateProduced=" + dateProduced +
                '}';
    }
}
