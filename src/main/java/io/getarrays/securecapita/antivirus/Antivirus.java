package io.getarrays.securecapita.antivirus;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.getarrays.securecapita.itauditing.Auditable;
import io.getarrays.securecapita.itinventory.Laptop;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;

@Entity
@Table(name = "antivirus")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@ToString(exclude = "laptop")
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(NON_DEFAULT)
public class Antivirus extends Auditable<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "name")
    private String name;

    @Column(nullable = false, unique = true, name = "`key`")
    private String key;

    @Column(name = "renew_time_interval")
    private Integer renewTimeInterval;

    private String version;

    private String vendor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AntivirusStatus status;

    @Column(name = "is_installed")
    private Boolean isInstalled;

    @Column(name = "license_expiration_date")
    private LocalDateTime licenseExpirationDate;

    @Column(name = "last_scan_date")
    private LocalDateTime lastScanDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "laptop_id", nullable = false)
    @JsonIgnore
    private Laptop laptop;
}
