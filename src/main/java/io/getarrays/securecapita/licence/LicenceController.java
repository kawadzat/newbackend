package io.getarrays.securecapita.licence;

import io.getarrays.securecapita.antivirus.AntivirusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/licence")
@RequiredArgsConstructor
public class LicenceController {

    private final AntivirusService antivirusService;
    private final LicenseService licenseService;


    @GetMapping(path = {"/all", "/getAll"})
    public ResponseEntity<?> getAllLicenses() {
        try {
            // Return licenses with laptop details for the table
            List<LicenseWithLaptopDetailsDto> licenses = licenseService.getAllWithLaptopDetails();
            System.out.println("Returning " + licenses.size() + " licenses from /all endpoint");
            return ResponseEntity.ok(licenses);
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback to basic license data
            try {
                List<LicenseDto> licenses = licenseService.getAll();
                return ResponseEntity.ok(licenses);
            } catch (Exception ex) {
                ex.printStackTrace();
                return ResponseEntity.ok(java.util.Collections.emptyList());
            }
        }
    }


    @GetMapping("/all-with-detailed-laptop-info")
    public ResponseEntity<?> getAllWithDetailedLaptopInfo() {
        try {
            System.out.println("=== getAllWithDetailedLaptopInfo called ===");
            // Return License data with laptop details
            List<LicenseWithLaptopDetailsDto> licenses = licenseService.getAllWithLaptopDetails();
            // Always return a list, even if empty
            if (licenses == null) {
                System.out.println("Licenses is null, returning empty list");
                licenses = java.util.Collections.emptyList();
            }
            System.out.println("Returning " + licenses.size() + " licenses");
            return ResponseEntity.ok(licenses);
        } catch (Throwable e) {
            e.printStackTrace();
            System.err.println("Error in getAllWithDetailedLaptopInfo: " + e.getMessage());
            System.err.println("Error class: " + e.getClass().getName());
            e.printStackTrace();
            // Return empty list instead of error to prevent frontend crash
            // Log the error but return empty array so table can still render
            return ResponseEntity.ok(java.util.Collections.emptyList());
        }
    }

    @PostMapping("/create/assign/laptop/{laptopId}")
    public ResponseEntity<?> createAndAssignLicenseToLaptop(
            @PathVariable Long laptopId,
            @Valid @RequestBody LicenseDto licenseDto) {
        try {
            LicenseDto createdLicense = licenseService.createAndAssignToLaptop(laptopId, licenseDto);
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("message", "License assigned to laptop successfully");
            response.put("data", createdLicense);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            java.util.Map<String, Object> errorResponse = new java.util.HashMap<>();
            errorResponse.put("message", e.getMessage());
            errorResponse.put("data", null);
            return ResponseEntity.status(400).body(errorResponse);
        } catch (Exception e) {
            e.printStackTrace();
            java.util.Map<String, Object> errorResponse = new java.util.HashMap<>();
            errorResponse.put("message", "Error assigning license to laptop: " + e.getMessage());
            errorResponse.put("data", null);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    // Additional CRUD endpoints for License
    @PostMapping(path = {"/", "/create"})
    public ResponseEntity<?> createLicense(@Valid @RequestBody LicenseDto licenseDto) {
        try {
            LicenseDto createdLicense = licenseService.create(licenseDto);
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("message", "License created successfully");
            response.put("data", createdLicense);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            java.util.Map<String, Object> errorResponse = new java.util.HashMap<>();
            errorResponse.put("message", "Error creating license: " + e.getMessage());
            errorResponse.put("data", null);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getLicenseById(@PathVariable Long id) {
        try {
            LicenseDto license = licenseService.getById(id);
            return ResponseEntity.ok(license);
        } catch (RuntimeException e) {
            java.util.Map<String, Object> errorResponse = new java.util.HashMap<>();
            errorResponse.put("message", e.getMessage());
            errorResponse.put("data", null);
            return ResponseEntity.status(404).body(errorResponse);
        }
    }

    @GetMapping("/laptop/{laptopId}")
    public ResponseEntity<?> getLicensesByLaptop(@PathVariable Long laptopId) {
        try {
            List<LicenseDto> licenses = licenseService.getLicensesByLaptop(laptopId);
            return ResponseEntity.ok(licenses);
        } catch (RuntimeException e) {
            java.util.Map<String, Object> errorResponse = new java.util.HashMap<>();
            errorResponse.put("message", e.getMessage());
            errorResponse.put("data", null);
            return ResponseEntity.status(404).body(errorResponse);
        }
    }
}

