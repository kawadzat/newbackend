package io.getarrays.securecapita.sslcertificate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SslcertificateRepository extends JpaRepository<Sslcertificate, Long> {
    List<Sslcertificate> findByDomainNameContainingIgnoreCase(String domainName);
    List<Sslcertificate> findByVendor(String vendor);
    List<Sslcertificate> findByExpiryDateBefore(LocalDate date);
    List<Sslcertificate> findByExpiryDateBetween(LocalDate startDate, LocalDate endDate);
}










