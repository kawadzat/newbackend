package io.getarrays.securecapita.sslcertificate;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.getarrays.securecapita.itauditing.Auditable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;

@Entity
@Table(name = "`sslcertificate`")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(NON_DEFAULT)
public class Sslcertificate extends Auditable<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Domain name is required")
    @Column(name = "domain_name", nullable = false)
    private String domainName;

    @Column(name = "validity")
    private String validity; // e.g., "1 year"

    @Column(name = "supplier")
    private String supplier;

    @NotNull(message = "Expiry date is required")
    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Column(name = "plan")
    private String plan; // e.g., "Standard"

    @Column(name = "vendor")
    private String vendor; // e.g., "DigiCert"

    @NotNull(message = "Purchase date is required")
    @Column(name = "purchase_date", nullable = false)
    private LocalDate purchaseDate;

    @Column(name = "certificate_serial_number")
    private String certificateSerialNumber;
}
