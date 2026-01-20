package io.getarrays.securecapita.laptopmoverequests;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/laptop/move/requests")
@AllArgsConstructor
public class LaptopMoveRequestController {
    private final LaptopMoveRequestService laptopMoveRequestService;

    @PostMapping(value = "/request", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> addRequest(@RequestBody @Valid LaptopMoveRequestDto laptopMoveRequestDto) {
        return laptopMoveRequestService.addRequest(laptopMoveRequestDto);
    }

    @PostMapping("/approve")
    public ResponseEntity<Object> approveRequest(@RequestParam("laptopMoveRequestId") Long requestId) {
        return laptopMoveRequestService.approve(requestId);
    }

    @PostMapping("/reject")
    public ResponseEntity<Object> rejectRequest(@RequestParam("laptopMoveRequestId") Long requestId) {
        return laptopMoveRequestService.reject(requestId);
    }
}

