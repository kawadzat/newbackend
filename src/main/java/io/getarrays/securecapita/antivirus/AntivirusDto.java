package io.getarrays.securecapita.antivirus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AntivirusDto {

    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Key is required")
    @JsonProperty("key")
    private String key;

    private Integer renewTimeInterval;

    private String version;

    private String vendor;

    @NotNull(message = "Status is required")
    private AntivirusStatus status;

    private Boolean isInstalled;

    @JsonProperty("licenseExpirationDate")
    private LocalDateTime licenseExpirationDate;

    private LocalDateTime lastScanDate;
}












