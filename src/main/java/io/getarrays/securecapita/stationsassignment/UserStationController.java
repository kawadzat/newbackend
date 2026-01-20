package io.getarrays.securecapita.stationsassignment;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.getarrays.securecapita.asserts.repo.StationRepository;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user/station")
public class UserStationController {
    private final UserStationService userStationService;
    private final StationRepository stationRepository;
    @GetMapping("/get")
    public ResponseEntity<?> getStations(){
        return userStationService.getCurrentUserStations();
    }

    @GetMapping("/getUser")
    public ResponseEntity<?> getUserStations(@RequestParam("userId")Long userId){
        return userStationService.getUserStations(userId);
    }

    @GetMapping("/getCount")
    public ResponseEntity<?> getUserStationsCount(){
        return ResponseEntity.ok(userStationService.getUserStationsCount());
    }

    @GetMapping("/add")
    public ResponseEntity<?> getAvailableStationsForUser(@RequestParam(name = "userId", required = false) Long userId){
        // Return stations assigned to the user (for dropdown display)
        // Try to get userId from authentication if not provided
        if (userId == null) {
            try {
                org.springframework.security.core.Authentication authentication = 
                    org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
                if (authentication != null && authentication.isAuthenticated() && 
                    authentication.getPrincipal() instanceof io.getarrays.securecapita.dto.UserDTO) {
                    userId = ((io.getarrays.securecapita.dto.UserDTO) authentication.getPrincipal()).getId();
                }
            } catch (Exception e) {
                // If can't get from auth, return error
            }
        }
        
        if (userId == null) {
            // No userId available - return empty array for NgFor compatibility
            // Frontend must provide userId or ensure user is authenticated
            System.out.println("WARNING: No userId provided for /api/v1/user/station/add - returning empty array. Frontend must pass userId parameter or ensure user is authenticated.");
            return ResponseEntity.ok(new java.util.ArrayList<>());
        }
        return userStationService.getAvailableStationsForUser(userId);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addStationToUser(@RequestParam(name = "userId") Long userId,@RequestParam(name = "stationId")Long stationId){
        return userStationService.addStationToUser(userId,stationId);
    }

    @DeleteMapping("/remove")
    public ResponseEntity<?> removeStationToUser(@RequestParam(name = "assignId") Long assignId){
        return userStationService.removeStationFromUser(assignId);
    }
}
