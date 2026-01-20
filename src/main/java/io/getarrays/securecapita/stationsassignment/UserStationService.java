package io.getarrays.securecapita.stationsassignment;

import io.getarrays.securecapita.asserts.model.Station;
import io.getarrays.securecapita.asserts.repo.StationRepository;
import io.getarrays.securecapita.domain.User;
import io.getarrays.securecapita.dto.UserDTO;
import io.getarrays.securecapita.exception.CustomMessage;
import io.getarrays.securecapita.repository.implementation.UserRepository1;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserStationService {
    private final UserStationRepo userStationRepo;
    private final UserRepository1 userRepository1;
    private final StationRepository stationRepository;

    public ResponseEntity<?> getCurrentUserStations() {
        try {
            Long userId = ((UserDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
            System.out.println("getCurrentUserStations called for userId: " + userId);
            List<UserStation> userStationList = userStationRepo.findAllByUser(userId);
            System.out.println("Found " + userStationList.size() + " user stations for userId: " + userId);
            
            List<UserStationDto> stationDtos = UserStationDto.fromEntities(userStationList);
            
            // Return array directly for frontend NgFor compatibility (profile component expects array)
            System.out.println("Returning " + stationDtos.size() + " stations as array");
            return ResponseEntity.ok(stationDtos);
        } catch (Exception e) {
            System.err.println("Error in getCurrentUserStations: " + e.getMessage());
            e.printStackTrace();
            // Return empty array on error for NgFor compatibility
            return ResponseEntity.status(500).body(new java.util.ArrayList<>());
        }
    }

    public ResponseEntity<?> addStationToUser(Long userId, Long stationId) {
        try {
            Optional<UserStation> optionalUserStation = userStationRepo.findAllByUserAndStation(userId, stationId);
            if (optionalUserStation.isEmpty()) {

                Optional<User> optionalUser = userRepository1.findById(userId);
                Optional<Station> optionalStation = stationRepository.findById(stationId);

                if (optionalUser.isEmpty() || optionalStation.isEmpty()) {
                    return ResponseEntity.badRequest().body(new CustomMessage("User or station is not valid, try again."));
                }

                // Try to get the authenticated user, but handle null case since endpoints are public
                User assignedBy = null;
                try {
                    if (SecurityContextHolder.getContext().getAuthentication() != null 
                        && SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof UserDTO) {
                        Long assignedById = ((UserDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
                        Optional<User> assignedByOpt = userRepository1.findById(assignedById);
                        if (assignedByOpt.isPresent()) {
                            assignedBy = assignedByOpt.get();
                        }
                    }
                } catch (Exception e) {
                    // If no authenticated user, use the user being assigned (self-assignment)
                    assignedBy = optionalUser.get();
                }
                
                // If still null, use the user being assigned
                if (assignedBy == null) {
                    assignedBy = optionalUser.get();
                }

                UserStation userStation = UserStation.builder()
                        .user(optionalUser.get())
                        .assignedBy(assignedBy)
                        .station(optionalStation.get())
                        .createdDate(new Timestamp(new Date().getTime()))
                        .build();

                // Save the userStation entity
                userStationRepo.save(userStation);
            }
            return ResponseEntity.ok(new CustomMessage("User successfully assigned to the station."));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new CustomMessage("Error adding station: " + e.getMessage()));
        }
    }

    public ResponseEntity<?> removeStationFromUser(Long assignId) {
        try {
            Optional<UserStation> optionalUserStation = userStationRepo.findById(assignId);
            if(optionalUserStation.isEmpty()){
                return ResponseEntity.badRequest().body(new CustomMessage("Assignment ID is not valid or already removed, try again."));
            }
            userStationRepo.delete(optionalUserStation.get());
            return ResponseEntity.ok(new CustomMessage("User removed from the station successfully."));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new CustomMessage("Error removing station assignment: " + e.getMessage()));
        }
    }

    public ResponseEntity<?> getUserStations(Long userId) {
        try {
            System.out.println("getUserStations called with userId: " + userId);
            List<UserStation> userStationList = userStationRepo.findAllByUser(userId);
            System.out.println("Found " + userStationList.size() + " user stations for userId: " + userId);
            
            List<UserStationDto> stationDtos = UserStationDto.fromEntities(userStationList);
            
            // Return array directly for frontend NgFor compatibility
            System.out.println("Returning " + stationDtos.size() + " stations as array");
            return ResponseEntity.ok(stationDtos);
        } catch (Exception e) {
            System.err.println("Error in getUserStations: " + e.getMessage());
            e.printStackTrace();
            // Return empty array on error for NgFor compatibility
            return ResponseEntity.status(500).body(new java.util.ArrayList<>());
        }
    }

    public UserStationCountDto getUserStationsCount() {
        Long userId = ((UserDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        Long count = userStationRepo.countByUser(userId);
        return new UserStationCountDto(count);
    }

    public ResponseEntity<?> getAvailableStationsForUser(Long userId) {
        // Return ALL stations for dropdown display (so user can select which ones to assign)
        try {
            System.out.println("getAvailableStationsForUser called with userId: " + userId);
            
            // Get ALL stations from database for assignment dropdown
            List<Station> allStations = stationRepository.findAll();
            System.out.println("Found " + allStations.size() + " total stations in database");
            
            List<java.util.Map<String, Object>> result = allStations.stream()
                    .map(station -> {
                        java.util.Map<String, Object> map = new java.util.HashMap<>();
                        map.put("id", station.getStation_id());
                        String stationName = station.getStationName();
                        // Handle null or empty station names
                        if (stationName == null || stationName.trim().isEmpty()) {
                            stationName = "Station " + station.getStation_id(); // Fallback name
                            System.out.println("WARNING: Station ID " + station.getStation_id() + " has no name, using fallback: " + stationName);
                        }
                        map.put("name", stationName);
                        System.out.println("Station ID: " + station.getStation_id() + ", Name: " + stationName);
                        return map;
                    })
                    .collect(java.util.stream.Collectors.toList());
            
            System.out.println("Returning " + result.size() + " stations as array for assignment dropdown");
            
            // Return array directly for frontend NgFor compatibility
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.err.println("Error in getAvailableStationsForUser: " + e.getMessage());
            e.printStackTrace();
            // Return empty array on error for NgFor compatibility
            return ResponseEntity.status(500).body(new java.util.ArrayList<>());
        }
    }
}
