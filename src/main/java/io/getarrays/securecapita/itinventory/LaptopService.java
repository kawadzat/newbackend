package io.getarrays.securecapita.itinventory;

import io.getarrays.securecapita.domain.User;
import io.getarrays.securecapita.dto.UserDTO;
import io.getarrays.securecapita.repository.UserRepository;
import io.getarrays.securecapita.repository.implementation.UserRepository1;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
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
        
        // Map all fields from DTO to entity
        laptop.setTitle(dto.getTitle());
        laptop.setDescription(dto.getDescription());
        laptop.setIssueDate(dto.getIssueDate());
        laptop.setReplacementDate(dto.getReplacementDate());
        laptop.setSerialNumber(dto.getSerialNumber() != null ? dto.getSerialNumber() : "");
        laptop.setManufacturer(dto.getManufacturer());
        laptop.setModel(dto.getModel());
        laptop.setRam(dto.getRam());
        laptop.setProcessor(dto.getProcessor());
        laptop.setStatus(dto.getStatus());
        laptop.setIssuedTo(dto.getIssuedTo());
        laptop.setEmail(dto.getEmail());
        laptop.setDepartment(dto.getDepartment());
        laptop.setDesignation(dto.getDesignation());
        
        // Handle issuedBy if provided
        if (dto.getIssuedByUser() != null && dto.getIssuedByUser().getId() != null) {
            userRepository1.findById(dto.getIssuedByUser().getId())
                    .ifPresent(laptop::setIssuedBy);
        } else if (currentUser != null && currentUser.getId() != null) {
            userRepository1.findById(currentUser.getId())
                    .ifPresent(laptop::setIssuedBy);
        }
        
        return laptop;
    }

    private LaptopDto entityToDto(Laptop entity) {
        return LaptopDto.builder()
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
                .issuedTo(entity.getIssuedTo())
                .email(entity.getEmail())
                .department(entity.getDepartment())
                .designation(entity.getDesignation())
                .build();
    }

    public List<LaptopDto> getAllLaptops() {
        List<Laptop> laptops = laptopRepository.findAll();
        return laptops.stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    public Long getLaptopCount() {
        return laptopRepository.count();
    }

}
