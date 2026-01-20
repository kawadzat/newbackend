package io.getarrays.securecapita.sslcertificate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SslcertificateDto {
    private Long id;

    @NotBlank(message = "Domain name is required")
    private String domainName;

    private String validity; // e.g., "1 year"

    private String supplier;

    @NotNull(message = "Expiry date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate expiryDate;

    private String plan; // e.g., "Standard"

    private String vendor; // e.g., "DigiCert"

    @NotNull(message = "Purchase date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate purchaseDate;

    private String certificateSerialNumber;
}


