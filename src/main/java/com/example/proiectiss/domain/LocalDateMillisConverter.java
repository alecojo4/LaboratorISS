package com.example.proiectiss.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Converter(autoApply = true)
public class LocalDateMillisConverter implements AttributeConverter<LocalDate, Long> {

    @Override
    public Long convertToDatabaseColumn(LocalDate locDate) {
        return (locDate == null ? null : locDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
    }

    @Override
    public LocalDate convertToEntityAttribute(Long millis) {
        return (millis == null ? null : Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate());
    }
}