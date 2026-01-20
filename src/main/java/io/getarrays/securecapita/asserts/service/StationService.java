package io.getarrays.securecapita.asserts.service;

import io.getarrays.securecapita.asserts.model.AssertEntity;
import io.getarrays.securecapita.asserts.model.Station;
import io.getarrays.securecapita.asserts.model.StationsAssetsStatDto;
import io.getarrays.securecapita.asserts.model.StationsStatDto;
import io.getarrays.securecapita.asserts.repo.AssertEntityRepository;
import io.getarrays.securecapita.asserts.repo.StationRepository;
import io.getarrays.securecapita.domain.User;
import io.getarrays.securecapita.stationsassignment.UserStation;
import io.getarrays.securecapita.stationsassignment.UserStationRepo;
import io.getarrays.securecapita.dto.StationItemStat;
import io.getarrays.securecapita.dto.StationStats;
import io.getarrays.securecapita.dto.UserDTO;
import io.getarrays.securecapita.exception.CustomMessage;
import io.getarrays.securecapita.exception.ResourceNotFoundException;
import io.getarrays.securecapita.repository.implementation.UserRepository1;
import io.getarrays.securecapita.roles.prerunner.ROLE_AUTH;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.*;
import java.util.Arrays;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StationService {
    private final StationRepository stationRepository;
    private final UserRepository1 userRepository1;
    private final AssertEntityRepository assertRepository;
    private final AssertService assertService;
    private final UserStationRepo userStationRepo;

    /* to create user */
    public Station createStation(Station newStation) {
        Station createdStation = stationRepository.save(newStation);
        return createdStation;
    }

    /* updating the user */
    public Station updateStation(Station station) {

        Station updatedStation = stationRepository.save(station);

        return updatedStation;
    }


    public Station getStationById(Long stationId) {

        Optional<Station> optionalStation = stationRepository.findById(stationId);
        boolean isPresent = optionalStation.isPresent();

        if (isPresent) {

            Station station = optionalStation.get();
            return station;
        }

        return null;


    }


    public ResponseEntity<?> addAssert(Long stationId, Long assertId) {
        Optional<Station> optionStation = stationRepository.findById(stationId);
        if (optionStation.isEmpty()) {
            return ResponseEntity.badRequest().body("Station does not exist!");
        }
        Optional<AssertEntity> optionalAssert = assertRepository.findById(assertId);
        if (optionalAssert.isEmpty()) {
            return ResponseEntity.badRequest().body("Assert does not exist!");
        }
        System.out.println("Ok");
        optionalAssert.get().setStation(optionStation.get());
        assertRepository.save(optionalAssert.get());
        return ResponseEntity.ok("Added Assert to Station");
    }

    public ResponseEntity<?> getAllStations(Long userIdParam, boolean all) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            List<Station> stations;
            
            System.out.println("getAllStations called with userIdParam: " + userIdParam + ", all: " + all);
            
            // If all=true is explicitly requested, return all stations (for Profile component)
            if (all) {
                stations = stationRepository.findAll();
                System.out.println("'all=true' parameter provided - returning ALL stations (" + stations.size() + " stations)");
            } else {
                Long userId = userIdParam;
                
                // If userIdParam is not provided, try to get it from authentication (for laptop form dropdown)
                if (userId == null) {
                    try {
                        System.out.println("Attempting to get userId from authentication. Authentication object: " + (authentication != null ? "exists" : "null"));
                        if (authentication != null) {
                            System.out.println("Authentication isAuthenticated: " + authentication.isAuthenticated());
                            System.out.println("Authentication principal type: " + (authentication.getPrincipal() != null ? authentication.getPrincipal().getClass().getName() : "null"));
                            
                            if (authentication.isAuthenticated()) {
                                Object principal = authentication.getPrincipal();
                                
                                // Try UserDTO first (most common case)
                                if (principal instanceof UserDTO) {
                                    userId = ((UserDTO) principal).getId();
                                    System.out.println("SUCCESS: Got userId from UserDTO: " + userId);
                                } 
                                // Try User domain object as fallback
                                else if (principal instanceof User) {
                                    userId = ((User) principal).getId();
                                    System.out.println("SUCCESS: Got userId from User: " + userId);
                                } 
                                // Try to extract from UserPrincipal if it exists
                                else if (principal instanceof io.getarrays.securecapita.domain.UserPrincipal) {
                                    userId = ((io.getarrays.securecapita.domain.UserPrincipal) principal).getUser().getId();
                                    System.out.println("SUCCESS: Got userId from UserPrincipal: " + userId);
                                }
                                else {
                                    System.out.println("WARNING: Principal is not a recognized type, cannot extract userId. Principal type: " + principal.getClass().getName());
                                    System.out.println("Principal toString: " + principal.toString());
                                }
                                
                                if (userId != null) {
                                    System.out.println("SUCCESS: No userIdParam provided, but got userId from authentication: " + userId);
                                }
                            } else {
                                System.out.println("WARNING: Authentication exists but is not authenticated");
                            }
                        } else {
                            System.out.println("WARNING: No authentication object available in SecurityContext");
                            System.out.println("This usually means:");
                            System.out.println("  1. No token was sent in the request");
                            System.out.println("  2. Token was invalid/expired and context was cleared");
                            System.out.println("  3. Token validation failed");
                            
                            // Try to get userId from request attribute (set by filter even if auth failed)
                            try {
                                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                                if (attributes != null) {
                                    Object userIdAttr = attributes.getRequest().getAttribute("userId");
                                    if (userIdAttr != null && userIdAttr instanceof Long) {
                                        userId = (Long) userIdAttr;
                                        System.out.println("SUCCESS: Got userId from request attribute (extracted from token even though auth failed): " + userId);
                                    }
                                }
                            } catch (Exception reqEx) {
                                System.out.println("Could not get userId from request attribute: " + reqEx.getMessage());
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("ERROR: Could not get userId from authentication: " + e.getMessage());
                        e.printStackTrace();
                        
                        // Fallback: Try to get userId from request attribute
                        try {
                            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                            if (attributes != null) {
                                Object userIdAttr = attributes.getRequest().getAttribute("userId");
                                if (userIdAttr != null && userIdAttr instanceof Long) {
                                    userId = (Long) userIdAttr;
                                    System.out.println("SUCCESS: Got userId from request attribute (fallback): " + userId);
                                }
                            }
                        } catch (Exception reqEx) {
                            System.out.println("Could not get userId from request attribute: " + reqEx.getMessage());
                        }
                    }
                }
                
                // If we have a userId (from param or authentication), return only stations assigned to that user
                if (userId != null) {
                    // Check if user has ALL_STATION permission - return all stations
                    boolean hasAllStationPermission = false;
                    try {
                        if (authentication != null && authentication.isAuthenticated() &&
                            authentication.getAuthorities() != null &&
                            authentication.getAuthorities().stream().anyMatch((r) -> r.getAuthority().contains(ROLE_AUTH.ALL_STATION.name()))) {
                            hasAllStationPermission = true;
                        }
                    } catch (Exception e) {
                        // If error checking permissions, default to user's assigned stations
                    }
                    
                    if (hasAllStationPermission) {
                        stations = stationRepository.findAll();
                        System.out.println("User has ALL_STATION permission, returning all stations: " + stations.size());
                    } else {
                        // Return only stations assigned to the user
                        System.out.println("Querying stations for userId: " + userId);
                        
                        // Use UserStationRepo to get assignments (this query is known to work)
                        List<UserStation> userStations = userStationRepo.findAllByUser(userId);
                        System.out.println("UserStation records found for userId " + userId + ": " + userStations.size());
                        
                        if (userStations.isEmpty()) {
                            System.out.println("WARNING: User " + userId + " has NO station assignments in UserStation table. User needs to be assigned stations via Profile component.");
                            stations = new ArrayList<>();
                        } else {
                            // Extract unique stations from UserStation records
                            stations = userStations.stream()
                                    .map(us -> us.getStation())
                                    .filter(station -> station != null)
                                    .distinct()
                                    .collect(java.util.stream.Collectors.toList());
                            
                            System.out.println("Extracted " + stations.size() + " unique stations from " + userStations.size() + " assignments");
                            if (stations.isEmpty()) {
                                System.out.println("WARNING: UserStation records exist but stations are null. Possible data inconsistency.");
                            } else {
                                System.out.println("Stations found: " + stations.stream().map(s -> s.getStation_id() + ":" + s.getStationName()).collect(java.util.stream.Collectors.joining(", ")));
                            }
                        }
                    }
                } else {
                    // No userIdParam and no authenticated user - return empty list
                    // User ID must be provided via token authentication or userId parameter
                    stations = new ArrayList<>();
                    System.out.println("==========================================");
                    System.out.println("ERROR: No userId parameter and no authentication!");
                    System.out.println("User ID is required but could not be extracted from token.");
                    System.out.println("Possible causes:");
                    System.out.println("  1. Token is expired or invalid");
                    System.out.println("  2. Token is not being sent in Authorization header");
                    System.out.println("  3. Token format is incorrect");
                    System.out.println("SOLUTION: Ensure valid token is sent with Authorization: Bearer <token>");
                    System.out.println("OR pass userId parameter: /station/getAll?userId=X");
                    System.out.println("==========================================");
                }
            }
            
            // Transform stations to the expected format
            List<Map<String, Object>> transformedStations = stations.stream()
                    .map(station -> {
                        Map<String, Object> map = new HashMap<>();
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
                    .toList();
            
            System.out.println("Transformed stations count: " + transformedStations.size());
            
            // Return array directly for frontend NgFor compatibility
            return ResponseEntity.ok(transformedStations);
        } catch (Exception e) {
            System.err.println("Error in getAllStations: " + e.getMessage());
            e.printStackTrace();
            // Return empty array on error for NgFor compatibility
            return ResponseEntity.status(500).body(new ArrayList<>());
        }
    }

    public ResponseEntity<?> addUser(Long stationId, Long userId) {

        return ResponseEntity.ok(new CustomMessage("Added User to Station"));
    }

    public ResponseEntity<?> checkAssets(Long stationId) {
        User user = userRepository1.findById(((UserDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId()).get();
        Optional<Station> stationOptional = stationRepository.findById(stationId);
        if (stationOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new CustomMessage("Station not found."));
        }
        if (!user.isStationAssigned(stationId)) {
            return ResponseEntity.status(401).body(new CustomMessage("You are not authorized in this station."));
        }
        stationRepository.editCheckAllAssetsForStation(stationOptional.get(), user);
        return ResponseEntity.ok(new CustomMessage("checked all assets till now."));
    }

    public ResponseEntity<?> getStats() {
        User user = userRepository1.findById(((UserDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId()).get();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getAuthorities().stream().anyMatch((r) -> r.getAuthority().contains(ROLE_AUTH.ALL_STATION.name()))) {
            ArrayList<StationItemStat> stationItemStats = stationRepository.findAssertItemStatsByAssetDisc();
            stationItemStats.forEach(stationItemStat -> {
                stationItemStat.setAssetsStats(assertService.getStatsByStation(stationItemStat.getStationId()));
            });
            return ResponseEntity.ok(StationStats.builder()
                    .totalStations(stationRepository.count())
                    .stationItemStats(stationItemStats)
                    .build());
        } else {
            if (user.isStationAssigned()) {
                List<StationItemStat> stationItemStats = stationRepository.findAssertItemStatsByUserStations(user.getId());
                stationItemStats.forEach(stationItemStat -> {
                    stationItemStat.setAssetsStats(assertService.getStatsByStation(stationItemStat.getStationId()));
                });
                return ResponseEntity.ok(StationStats.builder()
                        .totalStations(user.getStations().size())
                        .stationItemStats(stationItemStats)
                        .build());
            }
        }
        return ResponseEntity.badRequest().body(new CustomMessage("Not authorized for stats."));
    }

    public ResponseEntity<?> getStationStats() {
        User user = userRepository1.findById(((UserDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId()).get();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getAuthorities().stream().anyMatch((r) -> r.getAuthority().contains(ROLE_AUTH.ALL_STATION.name()))) {
            List<StationsStatDto> stationsStatDto = stationRepository.getStatforAllStations();
            List<StationsAssetsStatDto> stationsOfficesStatDtos = stationRepository.getAllAssetsStats();
            Map<String, List<StationsAssetsStatDto>> stationsToOfficesMap = new HashMap<>();
            for (StationsAssetsStatDto officeStatDto : stationsOfficesStatDtos) {
                stationsToOfficesMap.computeIfAbsent(officeStatDto.getStation(), k -> new ArrayList<>()).add(officeStatDto);
            }
            for (StationsStatDto stationStatDto : stationsStatDto) {
                if (stationStatDto.getAssets() > 0) {
                    stationStatDto.setAssetsList(stationsToOfficesMap.getOrDefault(stationStatDto.getName(), Collections.emptyList()));
                }
            }
            return ResponseEntity.ok(stationsStatDto);
        } else if (authentication.getAuthorities().stream().anyMatch((r) -> r.getAuthority().contains(ROLE_AUTH.VIEW_STATION.name()))) {
            if (!user.getStations().isEmpty()) {
                List<StationsStatDto> stationsStatDto = stationRepository.getStatForUserStations(user.getId());
                List<StationsAssetsStatDto> stationsOfficesStatDtos = stationRepository.getUserStationsAssetsStats(user.getId());
                System.out.println(stationsOfficesStatDtos);
                Map<String, List<StationsAssetsStatDto>> stationsToOfficesMap = new HashMap<>();
                for (StationsAssetsStatDto officeStatDto : stationsOfficesStatDtos) {
                    stationsToOfficesMap.computeIfAbsent(officeStatDto.getStation(), k -> new ArrayList<>()).add(officeStatDto);
                }
                for (StationsStatDto stationStatDto : stationsStatDto) {
                    if (stationStatDto.getAssets() > 0) {
                        stationStatDto.setAssetsList(stationsToOfficesMap.getOrDefault(stationStatDto.getName(), Collections.emptyList()));
                    }
                }
                return ResponseEntity.ok(stationsStatDto);
            }
        }
        return ResponseEntity.badRequest().body(new CustomMessage("Not authorized for stats."));
    }

    public ResponseEntity<List<Station>> getStationsByProvince(Long provinceId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDTO user = (UserDTO) authentication.getPrincipal();
        
        // Check if user has ALL_STATION permission
        boolean hasAllStationPermission = authentication.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().contains(ROLE_AUTH.ALL_STATION.name()));
        
        // Get all stations for the given province
        List<Station> stationsByProvince = stationRepository.findStationsByProvinceId(provinceId);
        
        if (hasAllStationPermission) {
            // Return all stations in the province for users with ALL_STATION permission
            return ResponseEntity.ok(stationsByProvince);
        }
        
        // For other users, return stations they are assigned to in this province
        List<Long> userStationIds = Arrays.stream(user.getStation().split(","))
            .map(Long::parseLong)
            .collect(Collectors.toList());
        
        List<Station> filteredStations = stationsByProvince.stream()
            .filter(station -> userStationIds.contains(station.getStation_id()))
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(filteredStations);
    }

    public Station findStationByIdOrThrow(Long id) {
        return stationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Station not found with id " + id));
    }
}
