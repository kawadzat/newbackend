package io.getarrays.securecapita.licence;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.getarrays.securecapita.itauditing.Auditable;
import io.getarrays.securecapita.itinventory.Laptop;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;

@Entity
@Table(name = "`license`")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(NON_DEFAULT)
public class License extends Auditable<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "License name is required")
    @Column(name = "license_name", nullable = false)
    private String licenseName;

    @NotNull(message = "License type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "license_type", nullable = false)
    private LicenseType licenseType;

    @NotBlank(message = "License key is required")
    @Column(name = "license_key", nullable = false, unique = true)
    private String licenseKey;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private LicenseStatus status;

    @NotNull(message = "Purchase date is required")
    @Column(name = "purchase_date", nullable = false)
    private LocalDate purchaseDate;

    @NotNull(message = "Expiry date is required")
    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Column(name = "vendor")
    private String vendor;

    @Column(name = "supplier")
    private String supplier;

    @Column(name = "version")
    private String version;

    @Column(name = "number_of_seats")
    private Integer numberOfSeats;

    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency")
    private Currency currency;

    @Column(name = "installation_path", length = 1000)
    private String installationPath;

    @Column(name = "assigned_to")
    private String assignedTo;

    @Column(name = "assigned_email")
    private String assignedEmail;

    @Column(name = "department")
    private String department;

    @Column(name = "station")
    private String station;

    @Column(name = "description", length = 2000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "laptop_id")
    @JsonIgnore
    private Laptop laptop;
}
