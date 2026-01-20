package io.getarrays.securecapita.licence;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.getarrays.securecapita.antivirus.AntivirusStatus;
import io.getarrays.securecapita.itinventory.LaptopDto;
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
public class LicenseWithLaptopInfoDto {
    private Long id;
    private String name;
    private String key;
    private Integer renewTimeInterval;
    private String version;
    private String vendor;
    private AntivirusStatus status;
    private Boolean isInstalled;
    private LocalDateTime licenseExpirationDate;
    private LocalDateTime lastScanDate;
    private LaptopDto laptop;
}












