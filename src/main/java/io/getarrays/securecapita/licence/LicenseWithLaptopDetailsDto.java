package io.getarrays.securecapita.licence;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.getarrays.securecapita.itinventory.LaptopDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LicenseWithLaptopDetailsDto {
    private Long id;
    
    // Support multiple field name formats for frontend compatibility
    @JsonProperty("licenseName")
    @com.fasterxml.jackson.annotation.JsonAlias({"name", "license_name"})
    private String licenseName;
    
    @JsonProperty("licenseType")
    @com.fasterxml.jackson.annotation.JsonAlias({"type", "license_type"})
    private LicenseType licenseType;
    
    @JsonProperty("licenseKey")
    @com.fasterxml.jackson.annotation.JsonAlias({"key", "license_key"})
    private String licenseKey;
    
    @JsonProperty("status")
    private LicenseStatus status;
    
    @JsonProperty("purchaseDate")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @com.fasterxml.jackson.annotation.JsonAlias({"purchase_date"})
    private LocalDate purchaseDate;
    
    @JsonProperty("expiryDate")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @com.fasterxml.jackson.annotation.JsonAlias({"expiry_date", "expirationDate", "expiration_date"})
    private LocalDate expiryDate;
    private String vendor;
    private String supplier;
    private String version;
    private Integer numberOfSeats;
    private BigDecimal price;
    private Currency currency;
    private String installationPath;
    private String assignedTo;
    private String assignedEmail;
    private String department;
    private String station;
    private String description;  // from license
    
    // Laptop information
    @com.fasterxml.jackson.annotation.JsonProperty("serialNumber")
    private String serialNumber;  // from laptop
    
    private String manufacturer;  // from laptop
    
    @com.fasterxml.jackson.annotation.JsonProperty("laptopDescription")
    private String laptopDescription; // from laptop - separate field to avoid conflict
    
    private LaptopDto laptop;
}

