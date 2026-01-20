package io.getarrays.securecapita.itinventory;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = {"/laptop", "/laptoplist"})
@RequiredArgsConstructor
public class LaptopController {

    @Autowired
    private LaptopService laptopService;

    @PostMapping(path = {"/", "/create"})
    public ResponseEntity<?> createLaptop(@RequestBody LaptopDto laptopDto) {
        try {
            LaptopDto createdLaptop = laptopService.createLaptop(null, laptopDto);
            // Return a Map structure that's easier for frontend to access
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("message", "Laptop Created Successfully");
            response.put("data", createdLaptop);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            java.util.Map<String, Object> errorResponse = new java.util.HashMap<>();
            errorResponse.put("message", "Error creating laptop: " + e.getMessage());
            errorResponse.put("data", null);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping(path={"/all", "/getAll"})
    public ResponseEntity<List<LaptopDto>> getAllLaptops() {
        return ResponseEntity.ok(laptopService.getAllLaptops());
    }

    @GetMapping(path="/dropdown")
    public ResponseEntity<List<LaptopDropdownDto>> getLaptopsForDropdown() {
        return ResponseEntity.ok(laptopService.getLaptopsForDropdown());
    }

    @GetMapping(path="/count")
    public ResponseEntity<Long> count() {
        return ResponseEntity.ok(laptopService.count());
    }

    @PutMapping(path = {"/{id}", "/update/{id}"})
    public ResponseEntity<?> updateLaptop(@PathVariable Long id, @RequestBody LaptopDto laptopDto) {
        try {
            LaptopDto updatedLaptop = laptopService.updateLaptop(id, laptopDto);
            // Return a Map structure that's easier for frontend to access
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("message", "Laptop Updated Successfully");
            response.put("data", updatedLaptop);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            // Handle validation errors (like status check)
            e.printStackTrace();
            java.util.Map<String, Object> errorResponse = new java.util.HashMap<>();
            errorResponse.put("message", e.getMessage());
            errorResponse.put("data", null);
            // Return 400 Bad Request for validation errors
            return ResponseEntity.status(400).body(errorResponse);
        } catch (Exception e) {
            e.printStackTrace();
            java.util.Map<String, Object> errorResponse = new java.util.HashMap<>();
            errorResponse.put("message", "Error updating laptop: " + e.getMessage());
            errorResponse.put("data", null);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }






}
