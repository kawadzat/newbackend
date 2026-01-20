package io.getarrays.securecapita.itinventory;

import io.getarrays.securecapita.domain.User;
import io.getarrays.securecapita.dto.UserDTO;
import io.getarrays.securecapita.repository.UserRepository;
import io.getarrays.securecapita.repository.implementation.UserRepository1;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class LaptopService {
    private final UserRepository<User> userRepository;

    private final UserRepository1 userRepository1;

    private final LaptopRepository laptopRepository;


    public LaptopDto createLaptop(UserDTO currentUser, LaptopDto laptopDto) {
        Laptop laptopEntity = dtoToEntity(currentUser, null, laptopDto); // Map DTO to entity
        Laptop savedLaptop = laptopRepository.save(laptopEntity);        // Save entity
        return entityToDto(savedLaptop);                                 // Map back to DTO
    }

    private Laptop dtoToEntity(UserDTO currentUser, Laptop existing, LaptopDto dto) {
        Laptop laptop = existing != null ? existing : new Laptop();
        laptop.setTitle(dto.getTitle());
        laptop.setDescription(dto.getDescription());
        laptop.setIssueDate(dto.getIssueDate());
        laptop.setReplacementDate(dto.getReplacementDate());
        laptop.setSerialNumber(dto.getSerialNumber());
        laptop.setManufacturer(dto.getManufacturer());
        laptop.setModel(dto.getModel());
        laptop.setRam(dto.getRam());
        laptop.setProcessor(dto.getProcessor());
        laptop.setStatus(dto.getStatus());
        laptop.setAssetType(dto.getAssetType());
        laptop.setIssuedTo(dto.getIssuedTo());
        laptop.setEmail(dto.getEmail());
        laptop.setDepartment(dto.getDepartment());
        laptop.setStation(dto.getStation());
        laptop.setDesignation(dto.getDesignation());
        // You may fetch User entity from userRepository based on currentUser if needed
        return laptop;
    }

    private LaptopDto entityToDto(Laptop entity) {
        if (entity == null) {
            return null;
        }
        
        LaptopDto.LaptopDtoBuilder builder = LaptopDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .issueDate(entity.getIssueDate())
                .replacementDate(entity.getReplacementDate())
                .serialNumber(entity.getSerialNumber())
                .manufacturer(entity.getManufacturer())
                .model(entity.getModel())
                .ram(entity.getRam())
                .processor(entity.getProcessor())
                .status(entity.getStatus())
                .AssetType(entity.getAssetType())
                .issuedTo(entity.getIssuedTo())
                .email(entity.getEmail())
                .department(entity.getDepartment())
                .station(entity.getStation())
                .designation(entity.getDesignation());
        
        // Convert issuedBy User to UserDTO if present
        if (entity.getIssuedBy() != null) {
            try {
                builder.issuedByUser(UserDTO.toDto(entity.getIssuedBy()));
            } catch (Exception e) {
                // Handle lazy loading or other exceptions
                System.err.println("Error converting issuedBy user to DTO: " + e.getMessage());
            }
        }
        
        // Convert users Set<User> to Set<UserDTO> if present
        if (entity.getUsers() != null && !entity.getUsers().isEmpty()) {
            try {
                Set<UserDTO> userDTOs = entity.getUsers().stream()
                        .map(UserDTO::toDto)
                        .collect(Collectors.toSet());
                builder.users(userDTOs);
            } catch (Exception e) {
                // Handle lazy loading or other exceptions
                System.err.println("Error converting users to DTOs: " + e.getMessage());
            }
        }
        
        return builder.build();
    }

    /**
     * Public method to convert Laptop entity to LaptopDto.
     * This method is used by other services like LicenseService.
     * 
     * @param laptop the Laptop entity to convert
     * @return LaptopDto or null if laptop is null
     */
    public LaptopDto convertToDto(Laptop laptop) {
        return entityToDto(laptop);
    }

    public List<LaptopDto> getAllLaptops() {
        List<Laptop> laptops = laptopRepository.findAll();
        return laptops.stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    public List<LaptopDropdownDto> getLaptopsForDropdown() {
        List<Laptop> laptops = laptopRepository.findAll();
        return laptops.stream()
                .map(laptop -> LaptopDropdownDto.builder()
                        .id(laptop.getId())
                        .label(laptop.getTitle() != null ? laptop.getTitle() : 
                               (laptop.getSerialNumber() != null ? laptop.getSerialNumber() : "Laptop #" + laptop.getId()))
                        .value(laptop.getSerialNumber() != null ? laptop.getSerialNumber() : String.valueOf(laptop.getId()))
                        .manufacturer(laptop.getManufacturer())
                        .model(laptop.getModel())
                        .build())
                .collect(Collectors.toList());
    }

    public Long count() {
        return laptopRepository.count();
    }

    public LaptopDto updateLaptop(Long id, LaptopDto laptopDto) {
        Laptop existingLaptop = laptopRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Laptop not found with id: " + id));
        
        Laptop updatedLaptop = dtoToEntity(null, existingLaptop, laptopDto);
        Laptop savedLaptop = laptopRepository.save(updatedLaptop);
        return entityToDto(savedLaptop);
    }

}
