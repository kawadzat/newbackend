package io.getarrays.securecapita.antivirus;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/antivirus")
@RequiredArgsConstructor
public class AntivirusController {

    private final AntivirusService antivirusService;


    // Laptop-related endpoints
    @PostMapping("/laptop/{laptopId}")
    public ResponseEntity<?> addAntivirusToLaptop(@PathVariable Long laptopId, @Valid @RequestBody AntivirusDto antivirusDto) {
        try {
            AntivirusDto createdAntivirus = antivirusService.addAntivirusToLaptop(laptopId, antivirusDto);
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("message", "Antivirus assigned to laptop successfully");
            response.put("data", createdAntivirus);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            java.util.Map<String, Object> errorResponse = new java.util.HashMap<>();
            errorResponse.put("message", e.getMessage());
            errorResponse.put("data", null);
            return ResponseEntity.status(400).body(errorResponse);
        } catch (Exception e) {
            e.printStackTrace();
            java.util.Map<String, Object> errorResponse = new java.util.HashMap<>();
            errorResponse.put("message", "Error assigning antivirus to laptop: " + e.getMessage());
            errorResponse.put("data", null);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

















    
    // CRUD Operations
    @PostMapping(path = {"/", "/create"})
    public ResponseEntity<?> createAntivirus(@Valid @RequestBody AntivirusDto antivirusDto) {
        try {
            AntivirusDto createdAntivirus = antivirusService.create(antivirusDto);
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("message", "Antivirus record created successfully");
            response.put("data", createdAntivirus);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            java.util.Map<String, Object> errorResponse = new java.util.HashMap<>();
            errorResponse.put("message", "Error creating antivirus record: " + e.getMessage());
            errorResponse.put("data", null);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping(path = {"/all", "/getAll"})
    public ResponseEntity<List<AntivirusDto>> getAllAntivirus() {
        return ResponseEntity.ok(antivirusService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAntivirusById(@PathVariable Long id) {
        try {
            AntivirusDto antivirus = antivirusService.getById(id);
            return ResponseEntity.ok(antivirus);
        } catch (RuntimeException e) {
            java.util.Map<String, Object> errorResponse = new java.util.HashMap<>();
            errorResponse.put("message", e.getMessage());
            errorResponse.put("data", null);
            return ResponseEntity.status(404).body(errorResponse);
        }
    }

    @PutMapping(path = {"/{id}", "/update/{id}"})
    public ResponseEntity<?> updateAntivirus(@PathVariable Long id, @Valid @RequestBody AntivirusDto antivirusDto) {
        try {
            AntivirusDto updatedAntivirus = antivirusService.update(id, antivirusDto);
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("message", "Antivirus record updated successfully");
            response.put("data", updatedAntivirus);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            java.util.Map<String, Object> errorResponse = new java.util.HashMap<>();
            errorResponse.put("message", e.getMessage());
            errorResponse.put("data", null);
            return ResponseEntity.status(404).body(errorResponse);
        } catch (Exception e) {
            e.printStackTrace();
            java.util.Map<String, Object> errorResponse = new java.util.HashMap<>();
            errorResponse.put("message", "Error updating antivirus record: " + e.getMessage());
            errorResponse.put("data", null);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAntivirus(@PathVariable Long id) {
        try {
            antivirusService.delete(id);
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("message", "Antivirus record deleted successfully");
            response.put("data", null);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            java.util.Map<String, Object> errorResponse = new java.util.HashMap<>();
            errorResponse.put("message", e.getMessage());
            errorResponse.put("data", null);
            return ResponseEntity.status(404).body(errorResponse);
        } catch (Exception e) {
            e.printStackTrace();
            java.util.Map<String, Object> errorResponse = new java.util.HashMap<>();
            errorResponse.put("message", "Error deleting antivirus record: " + e.getMessage());
            errorResponse.put("data", null);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    


    @GetMapping("/laptop/{laptopId}")
    public ResponseEntity<?> getAntivirusByLaptop(@PathVariable Long laptopId) {
        try {
            List<AntivirusDto> antivirusList = antivirusService.getAntivirusByLaptop(laptopId);
            return ResponseEntity.ok(antivirusList);
        } catch (RuntimeException e) {
            java.util.Map<String, Object> errorResponse = new java.util.HashMap<>();
            errorResponse.put("message", e.getMessage());
            errorResponse.put("data", null);
            return ResponseEntity.status(404).body(errorResponse);
        }
    }

    @DeleteMapping("/laptop/{laptopId}/{antivirusId}")
    public ResponseEntity<?> removeAntivirusFromLaptop(@PathVariable Long laptopId, @PathVariable Long antivirusId) {
        try {
            antivirusService.removeAntivirusFromLaptop(laptopId, antivirusId);
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("message", "Antivirus removed from laptop successfully");
            response.put("data", null);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            java.util.Map<String, Object> errorResponse = new java.util.HashMap<>();
            errorResponse.put("message", e.getMessage());
            errorResponse.put("data", null);
            return ResponseEntity.status(400).body(errorResponse);
        } catch (Exception e) {
            e.printStackTrace();
            java.util.Map<String, Object> errorResponse = new java.util.HashMap<>();
            errorResponse.put("message", "Error removing antivirus from laptop: " + e.getMessage());
            errorResponse.put("data", null);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

} 