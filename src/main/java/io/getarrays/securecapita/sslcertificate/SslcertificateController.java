package io.getarrays.securecapita.sslcertificate;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({"/api/v1/ssl-certificate", "/sslcertificate"})
@RequiredArgsConstructor
public class SslcertificateController {

    private final SslcertificateService sslcertificateService;

    @PostMapping(path = {"/", "/create"})
    public ResponseEntity<?> createSslCertificate(@Valid @RequestBody SslcertificateDto sslcertificateDto) {
        try {
            SslcertificateDto created = sslcertificateService.create(sslcertificateDto);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "SSL Certificate created successfully");
            response.put("data", created);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error creating SSL Certificate: " + e.getMessage());
            errorResponse.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllSslCertificates() {
        try {
            List<SslcertificateDto> certificates = sslcertificateService.getAll();
            return ResponseEntity.ok(certificates);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(List.of());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSslCertificateById(@PathVariable Long id) {
        try {
            SslcertificateDto certificate = sslcertificateService.getById(id);
            return ResponseEntity.ok(certificate);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            errorResponse.put("data", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSslCertificate(
            @PathVariable Long id,
            @Valid @RequestBody SslcertificateDto sslcertificateDto) {
        try {
            SslcertificateDto updated = sslcertificateService.update(id, sslcertificateDto);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "SSL Certificate updated successfully");
            response.put("data", updated);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            errorResponse.put("data", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error updating SSL Certificate: " + e.getMessage());
            errorResponse.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSslCertificate(@PathVariable Long id) {
        try {
            sslcertificateService.delete(id);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "SSL Certificate deleted successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error deleting SSL Certificate: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/expiring-soon")
    public ResponseEntity<?> getExpiringSoon(
            @RequestParam(value = "days", defaultValue = "30") int days) {
        try {
            List<SslcertificateDto> certificates = sslcertificateService.getExpiringSoon(days);
            return ResponseEntity.ok(certificates);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(List.of());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchByDomainName(@RequestParam String domainName) {
        try {
            List<SslcertificateDto> certificates = sslcertificateService.searchByDomainName(domainName);
            return ResponseEntity.ok(certificates);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(List.of());
        }
    }

    @GetMapping("/vendor/{vendor}")
    public ResponseEntity<?> getByVendor(@PathVariable String vendor) {
        try {
            List<SslcertificateDto> certificates = sslcertificateService.getByVendor(vendor);
            return ResponseEntity.ok(certificates);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(List.of());
        }
    }
}

