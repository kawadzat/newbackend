package io.getarrays.securecapita.itinventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LaptopDropdownDto {
    private Long id;
    private String label;
    private String value;
    private String manufacturer;
    private String model;
}


