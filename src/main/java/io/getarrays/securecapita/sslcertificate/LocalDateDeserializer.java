package io.getarrays.securecapita.sslcertificate;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class LocalDateDeserializer extends JsonDeserializer<LocalDate> {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    @Override
    public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String dateString = p.getText();
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }
        
        try {
            // Try to parse as ISO datetime string (e.g., "2026-01-19T22:00:00.000Z")
            if (dateString.contains("T") || dateString.contains("Z")) {
                Instant instant = Instant.parse(dateString);
                return LocalDate.ofInstant(instant, ZoneId.systemDefault());
            }
            // Try to parse as date-only string (e.g., "2026-01-19")
            else if (dateString.length() == 10) {
                return LocalDate.parse(dateString, DATE_FORMATTER);
            }
            // Try standard ISO date parsing
            else {
                return LocalDate.parse(dateString);
            }
        } catch (DateTimeParseException e) {
            throw new IOException("Unable to parse date: " + dateString, e);
        }
    }
}










