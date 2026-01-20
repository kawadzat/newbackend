package io.getarrays.securecapita.sslcertificate;

import io.getarrays.securecapita.domain.User;
import io.getarrays.securecapita.repository.implementation.UserRepository1;
import io.getarrays.securecapita.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SslcertificateServiceImpl implements SslcertificateService {

    private final SslcertificateRepository sslcertificateRepository;
    private final EmailService emailService;
    private final UserRepository1 userRepository1;

    @Override
    public SslcertificateDto create(SslcertificateDto sslcertificateDto) {
        Sslcertificate sslcertificate = mapToEntity(sslcertificateDto);
        Sslcertificate saved = sslcertificateRepository.save(sslcertificate);
        return mapToDto(saved);
    }

    @Override
    public SslcertificateDto update(Long id, SslcertificateDto sslcertificateDto) {
        Sslcertificate existing = sslcertificateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SSL Certificate not found with id: " + id));
        
        existing.setDomainName(sslcertificateDto.getDomainName());
        existing.setValidity(sslcertificateDto.getValidity());
        existing.setSupplier(sslcertificateDto.getSupplier());
        existing.setExpiryDate(sslcertificateDto.getExpiryDate());
        existing.setPlan(sslcertificateDto.getPlan());
        existing.setVendor(sslcertificateDto.getVendor());
        existing.setPurchaseDate(sslcertificateDto.getPurchaseDate());
        existing.setCertificateSerialNumber(sslcertificateDto.getCertificateSerialNumber());
        
        Sslcertificate updated = sslcertificateRepository.save(existing);
        return mapToDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public SslcertificateDto getById(Long id) {
        Sslcertificate sslcertificate = sslcertificateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SSL Certificate not found with id: " + id));
        return mapToDto(sslcertificate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SslcertificateDto> getAll() {
        return sslcertificateRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        if (!sslcertificateRepository.existsById(id)) {
            throw new RuntimeException("SSL Certificate not found with id: " + id);
        }
        sslcertificateRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SslcertificateDto> getExpiringSoon(int days) {
        LocalDate targetDate = LocalDate.now().plusDays(days);
        List<Sslcertificate> expiring = sslcertificateRepository.findByExpiryDateBetween(
                LocalDate.now(), targetDate);
        return expiring.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SslcertificateDto> searchByDomainName(String domainName) {
        List<Sslcertificate> certificates = sslcertificateRepository.findByDomainNameContainingIgnoreCase(domainName);
        return certificates.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SslcertificateDto> getByVendor(String vendor) {
        List<Sslcertificate> certificates = sslcertificateRepository.findByVendor(vendor);
        return certificates.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private Sslcertificate mapToEntity(SslcertificateDto dto) {
        return Sslcertificate.builder()
                .id(dto.getId())
                .domainName(dto.getDomainName())
                .validity(dto.getValidity())
                .supplier(dto.getSupplier())
                .expiryDate(dto.getExpiryDate())
                .plan(dto.getPlan())
                .vendor(dto.getVendor())
                .purchaseDate(dto.getPurchaseDate())
                .certificateSerialNumber(dto.getCertificateSerialNumber())
                .build();
    }

    private SslcertificateDto mapToDto(Sslcertificate entity) {
        return SslcertificateDto.builder()
                .id(entity.getId())
                .domainName(entity.getDomainName())
                .validity(entity.getValidity())
                .supplier(entity.getSupplier())
                .expiryDate(entity.getExpiryDate())
                .plan(entity.getPlan())
                .vendor(entity.getVendor())
                .purchaseDate(entity.getPurchaseDate())
                .certificateSerialNumber(entity.getCertificateSerialNumber())
                .build();
    }

    /**
     * Scheduled task to check for SSL certificates expiring in 90 days
     * and send notifications to DEPUTY_HEAD_IT and DATA_PROTECTION_OFFICER
     * Runs daily at 8:00 AM
     */
    @Scheduled(cron = "0 0 8 * * ?")
    @Transactional(readOnly = true)
    public void checkAndNotifyExpiringCertificates() {
        log.info("Checking for SSL certificates expiring in 90 days...");
        
        LocalDate today = LocalDate.now();
        LocalDate targetDate = today.plusDays(90);
        
        // Find certificates expiring exactly 90 days from now (with 1 day tolerance)
        LocalDate startDate = targetDate.minusDays(1);
        LocalDate endDate = targetDate.plusDays(1);
        
        List<Sslcertificate> expiringCertificates = sslcertificateRepository.findByExpiryDateBetween(startDate, endDate);
        
        if (expiringCertificates.isEmpty()) {
            log.info("No SSL certificates found expiring in 90 days.");
            return;
        }
        
        log.info("Found {} SSL certificate(s) expiring in 90 days", expiringCertificates.size());
        
        // Find users with DEPUTY_HEAD_IT and DATA_PROTECTION_OFFICER roles
        List<String> roleNames = Arrays.asList("DEPUTY_HEAD_IT", "DATA_PROTECTION_OFFICER");
        List<User> usersToNotify = userRepository1.findByRoleNameIn(roleNames);
        
        if (usersToNotify.isEmpty()) {
            log.warn("No users found with roles: {}", roleNames);
            return;
        }
        
        log.info("Found {} user(s) to notify", usersToNotify.size());
        
        // Send notifications to each user
        for (User user : usersToNotify) {
            if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                String subject = "SSL Certificate Expiration Alert - 90 Days Remaining";
                String content = buildNotificationEmailContent(expiringCertificates);
                
                try {
                    emailService.sendEmail(user.getEmail(), subject, content);
                    log.info("Notification sent to {} ({})", user.getEmail(), user.getFirstName());
                } catch (Exception e) {
                    log.error("Failed to send notification to {}: {}", user.getEmail(), e.getMessage());
                }
            }
        }
    }
    
    private String buildNotificationEmailContent(List<Sslcertificate> certificates) {
        StringBuilder content = new StringBuilder();
        content.append("<html><head><style>");
        content.append("body { font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f5f5f5; }");
        content.append(".card { background-color: #ffffff; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); max-width: 600px; margin: auto; padding: 20px; }");
        content.append("table { width: 100%; border-collapse: collapse; margin-top: 20px; }");
        content.append("th, td { padding: 12px; text-align: left; border-bottom: 1px solid #ddd; }");
        content.append("th { background-color: #4CAF50; color: white; }");
        content.append(".warning { color: #ff9800; font-weight: bold; }");
        content.append("</style></head><body>");
        
        content.append("<div class='card'>");
        content.append("<h2 style='color: #ff9800;'>SSL Certificate Expiration Alert</h2>");
        content.append("<p class='warning'>Warning: The following SSL certificate(s) will expire in approximately 90 days:</p>");
        
        content.append("<table>");
        content.append("<tr>");
        content.append("<th>Domain Name</th>");
        content.append("<th>Vendor</th>");
        content.append("<th>Expiry Date</th>");
        content.append("<th>Serial Number</th>");
        content.append("</tr>");
        
        for (Sslcertificate cert : certificates) {
            content.append("<tr>");
            content.append("<td>").append(cert.getDomainName() != null ? cert.getDomainName() : "N/A").append("</td>");
            content.append("<td>").append(cert.getVendor() != null ? cert.getVendor() : "N/A").append("</td>");
            content.append("<td>").append(cert.getExpiryDate() != null ? cert.getExpiryDate().toString() : "N/A").append("</td>");
            content.append("<td>").append(cert.getCertificateSerialNumber() != null ? cert.getCertificateSerialNumber() : "N/A").append("</td>");
            content.append("</tr>");
        }
        
        content.append("</table>");
        content.append("<p style='margin-top: 20px;'>Please take necessary action to renew these certificates before they expire.</p>");
        content.append("<p>The Support Team</p>");
        content.append("</div></body></html>");
        
        return content.toString();
    }
}
