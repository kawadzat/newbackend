package io.getarrays.securecapita.itinventory;

import io.getarrays.securecapita.exception.CustomMessage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = {"/laptop", "/laptoplist"})
@RequiredArgsConstructor
public class LaptopController {

    @Autowired
    private LaptopService laptopService;

    @PostMapping("/create")
    public ResponseEntity<?> createLaptop(@RequestBody @Valid LaptopDto laptopDto) throws Exception {
        return ResponseEntity.ok(new CustomMessage("Laptop Created Successfully", laptopService.createLaptop(null,
                laptopDto)));
    }

    @GetMapping(path={"/all", "/getAll"})
    public ResponseEntity<List<LaptopDto>> getAllLaptops() {
        return ResponseEntity.ok(laptopService.getAllLaptops());
    }

    @GetMapping(path={"/count", "/getCount"})
    public ResponseEntity<Long> getLaptopCount() {
        return ResponseEntity.ok(laptopService.getLaptopCount());
    }






}
