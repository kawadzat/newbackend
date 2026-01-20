package io.getarrays.securecapita.laptopmoverequests;

import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LaptopMoveRequestDto {
    @NotNull
    private Long laptopId;
    @NotNull
    private Long stationId;
    @NotNull
    private String reason;
    
    // Custom setters to handle both string and number types from frontend
    @JsonSetter("laptopId")
    public void setLaptopId(Object value) {
        if (value == null) {
            this.laptopId = null;
        } else if (value instanceof String) {
            try {
                this.laptopId = Long.parseLong((String) value);
            } catch (NumberFormatException e) {
                this.laptopId = null;
            }
        } else if (value instanceof Number) {
            this.laptopId = ((Number) value).longValue();
        } else {
            this.laptopId = null;
        }
    }
    
    @JsonSetter("stationId")
    public void setStationId(Object value) {
        if (value == null) {
            this.stationId = null;
        } else if (value instanceof String) {
            try {
                this.stationId = Long.parseLong((String) value);
            } catch (NumberFormatException e) {
                this.stationId = null;
            }
        } else if (value instanceof Number) {
            this.stationId = ((Number) value).longValue();
        } else {
            this.stationId = null;
        }
    }
}

