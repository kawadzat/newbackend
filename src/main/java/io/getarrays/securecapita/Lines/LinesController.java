package io.getarrays.securecapita.Lines;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/lines")
public class LinesController {



    private final LinesService linesService;

    public LinesController(LinesService linesService) {
        this.linesService = linesService;
    }

    @PostMapping("/create")
    public ResponseEntity<Lines> create(@RequestBody Lines lines) {
        return ResponseEntity.ok(linesService.create(lines));
    }

    @GetMapping("/all")
    public ResponseEntity<java.util.List<Lines>> getAll() {
        return ResponseEntity.ok(linesService.findAll());
    }

    @GetMapping("/count")
    public ResponseEntity<Long> count() {
        return ResponseEntity.ok(linesService.count());
    }





}
