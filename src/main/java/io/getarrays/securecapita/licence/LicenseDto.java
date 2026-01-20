package io.getarrays.securecapita.licence;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class LicenseDto {

    private Long id;

    @NotBlank(message = "License name is required")
    @JsonProperty("licenseName")
    private String licenseName;

    @NotNull(message = "License type is required")
    @JsonProperty("licenseType")
    private LicenseType licenseType;

    @NotBlank(message = "License key is required")
    @JsonProperty("licenseKey")
    private String licenseKey;

    @NotNull(message = "Status is required")
    private LicenseStatus status;

    @NotNull(message = "Purchase date is required")
    @JsonProperty("purchaseDate")
    private LocalDate purchaseDate;

    @NotNull(message = "Expiry date is required")
    @JsonProperty("expiryDate")
    private LocalDate expiryDate;

    private String vendor;

    private String supplier;

    private String version;

    @JsonProperty("numberOfSeats")
    private Integer numberOfSeats;

    private BigDecimal price;

    private Currency currency;

    @JsonProperty("installationPath")
    private String installationPath;

    @JsonProperty("assignedTo")
    private String assignedTo;

    @JsonProperty("assignedEmail")
    private String assignedEmail;

    private String department;

    private String station;

    private String description;
}












