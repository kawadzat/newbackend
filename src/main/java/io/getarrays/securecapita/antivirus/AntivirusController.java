package io.getarrays.securecapita.antivirus;

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
    public ResponseEntity<Antivirus> addAntivirusToLaptop(@PathVariable Long laptopId, @RequestBody Antivirus antivirus) {
        return new ResponseEntity<>(antivirusService.addAntivirusToLaptop(laptopId, antivirus), HttpStatus.CREATED);
    }

    @GetMapping("/laptop/{laptopId}")
    public ResponseEntity<List<Antivirus>> getAntivirusByLaptop(@PathVariable Long laptopId) {
        return ResponseEntity.ok(antivirusService.getAntivirusByLaptop(laptopId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Antivirus>> getAllAntivirus() {
        return ResponseEntity.ok(antivirusService.getAll());
    }

} 