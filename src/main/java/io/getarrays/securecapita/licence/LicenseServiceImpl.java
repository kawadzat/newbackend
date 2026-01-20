package io.getarrays.securecapita.licence;

import io.getarrays.securecapita.itinventory.Laptop;
import io.getarrays.securecapita.itinventory.LaptopDto;
import io.getarrays.securecapita.itinventory.LaptopRepository;
import io.getarrays.securecapita.itinventory.LaptopService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LicenseServiceImpl implements LicenseService {

    private final LicenseRepository licenseRepository;
    private final LaptopRepository laptopRepository;
    private final LaptopService laptopService;

    public LicenseServiceImpl(LicenseRepository licenseRepository, 
                             LaptopRepository laptopRepository, 
                             LaptopService laptopService) {
        this.licenseRepository = licenseRepository;
        this.laptopRepository = laptopRepository;
        this.laptopService = laptopService;
    }

    @Override
    public LicenseDto create(LicenseDto licenseDto) {
        License license = dtoToEntity(licenseDto);
        License savedLicense = licenseRepository.save(license);
        return entityToDto(savedLicense);
    }

    @Override
    public LicenseDto update(Long id, LicenseDto licenseDto) {
        License existingLicense = licenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("License not found with id: " + id));
        
        updateEntityFromDto(existingLicense, licenseDto);
        License updatedLicense = licenseRepository.save(existingLicense);
        return entityToDto(updatedLicense);
    }

    @Override
    public LicenseDto getById(Long id) {
        License license = licenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("License not found with id: " + id));
        return entityToDto(license);
    }

    @Override
    public List<LicenseDto> getAll() {
        List<License> licenseList = licenseRepository.findAll();
        return licenseList.stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        if (!licenseRepository.existsById(id)) {
            throw new RuntimeException("License not found with id: " + id);
        }
        licenseRepository.deleteById(id);
    }

    @Override
    public LicenseDto createAndAssignToLaptop(Long laptopId, LicenseDto licenseDto) {
        Laptop laptop = laptopRepository.findById(laptopId)
                .orElseThrow(() -> new RuntimeException("Laptop not found with id: " + laptopId));

        License license = dtoToEntity(licenseDto);
        license.setLaptop(laptop);
        
        License savedLicense = licenseRepository.save(license);
        return entityToDto(savedLicense);
    }

    @Override
    public List<LicenseDto> getLicensesByLaptop(Long laptopId) {
        if (!laptopRepository.existsById(laptopId)) {
            throw new RuntimeException("Laptop not found with id: " + laptopId);
        }
        List<License> licenseList = licenseRepository.findAll();
        return licenseList.stream()
                .filter(license -> license.getLaptop() != null && license.getLaptop().getId().equals(laptopId))
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void removeLicenseFromLaptop(Long laptopId, Long licenseId) {
        if (!laptopRepository.existsById(laptopId)) {
            throw new RuntimeException("Laptop not found with id: " + laptopId);
        }

        License license = licenseRepository.findById(licenseId)
                .orElseThrow(() -> new RuntimeException("License not found with id: " + licenseId));

        if (license.getLaptop() == null || !license.getLaptop().getId().equals(laptopId)) {
            throw new IllegalArgumentException("License does not belong to the specified laptop");
        }

        license.setLaptop(null);
        licenseRepository.save(license);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LicenseWithLaptopDetailsDto> getAllWithLaptopDetails() {
        try {
            // Use findAll() - the @Transactional annotation will handle lazy loading
            List<License> licenseList = licenseRepository.findAll();
            
            if (licenseList == null || licenseList.isEmpty()) {
                System.out.println("No licenses found in database");
                return java.util.Collections.emptyList();
            }
            System.out.println("Found " + licenseList.size() + " licenses in database");
            
            List<LicenseWithLaptopDetailsDto> result = new java.util.ArrayList<>();
            for (License license : licenseList) {
                try {
                    LicenseWithLaptopDetailsDto dto = convertToLicenseWithLaptopDetailsDto(license);
                    if (dto != null) {
                        result.add(dto);
                    }
                } catch (Exception e) {
                    System.err.println("Error converting license " + license.getId() + ": " + e.getMessage());
                    e.printStackTrace();
                    // Skip this license and continue with others
                }
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error in getAllWithLaptopDetails: " + e.getMessage());
            e.printStackTrace();
            // Return empty list instead of throwing exception to prevent 500 error
            return java.util.Collections.emptyList();
        }
    }

    private LicenseWithLaptopDetailsDto convertToLicenseWithLaptopDetailsDto(License license) {
        if (license == null) {
            return null;
        }
        
        try {
            LaptopDto laptopDto = null;
            String serialNumber = null;
            String manufacturer = null;
            String laptopDescription = null;
            
            // Safely access laptop within transaction
            try {
                Laptop laptop = license.getLaptop();
                if (laptop != null) {
                    try {
                        laptopDto = laptopService.convertToDto(laptop);
                        serialNumber = laptop.getSerialNumber();
                        manufacturer = laptop.getManufacturer();
                        laptopDescription = laptop.getDescription();
                    } catch (Exception e) {
                        System.err.println("Error converting laptop to DTO for license " + license.getId() + ": " + e.getMessage());
                        e.printStackTrace();
                        // Continue without laptop data
                    }
                }
            } catch (org.hibernate.LazyInitializationException e) {
                System.err.println("Lazy initialization exception for license " + license.getId() + ": " + e.getMessage());
                // Laptop is not loaded, continue without it
            } catch (Exception e) {
                System.err.println("Error accessing laptop for license " + license.getId() + ": " + e.getMessage());
                e.printStackTrace();
                // Continue without laptop data
            }
        
            return LicenseWithLaptopDetailsDto.builder()
                    .id(license.getId())
                    .licenseName(license.getLicenseName())
                    .licenseType(license.getLicenseType())
                    .licenseKey(license.getLicenseKey())
                    .status(license.getStatus())
                    .purchaseDate(license.getPurchaseDate())
                    .expiryDate(license.getExpiryDate())
                    .vendor(license.getVendor())
                    .supplier(license.getSupplier())
                    .version(license.getVersion())
                    .numberOfSeats(license.getNumberOfSeats())
                    .price(license.getPrice())
                    .currency(license.getCurrency())
                    .installationPath(license.getInstallationPath())
                    .assignedTo(license.getAssignedTo())
                    .assignedEmail(license.getAssignedEmail())
                    .department(license.getDepartment())
                    .station(license.getStation())
                    .description(license.getDescription())
                    .serialNumber(serialNumber)
                    .manufacturer(manufacturer)
                    .laptopDescription(laptopDescription)
                    .laptop(laptopDto)
                    .build();
        } catch (Exception e) {
            System.err.println("Error converting license to DTO for license ID " + (license != null ? license.getId() : "null") + ": " + e.getMessage());
            e.printStackTrace();
            // Return null and let the stream filter it out
            return null;
        }
    }

    // DTO conversion methods
    private License dtoToEntity(LicenseDto dto) {
        return License.builder()
                .id(dto.getId())
                .licenseName(dto.getLicenseName())
                .licenseType(dto.getLicenseType())
                .licenseKey(dto.getLicenseKey())
                .status(dto.getStatus())
                .purchaseDate(dto.getPurchaseDate())
                .expiryDate(dto.getExpiryDate())
                .vendor(dto.getVendor())
                .supplier(dto.getSupplier())
                .version(dto.getVersion())
                .numberOfSeats(dto.getNumberOfSeats())
                .price(dto.getPrice())
                .currency(dto.getCurrency())
                .installationPath(dto.getInstallationPath())
                .assignedTo(dto.getAssignedTo())
                .assignedEmail(dto.getAssignedEmail())
                .department(dto.getDepartment())
                .station(dto.getStation())
                .description(dto.getDescription())
                .build();
    }

    private LicenseDto entityToDto(License entity) {
        return LicenseDto.builder()
                .id(entity.getId())
                .licenseName(entity.getLicenseName())
                .licenseType(entity.getLicenseType())
                .licenseKey(entity.getLicenseKey())
                .status(entity.getStatus())
                .purchaseDate(entity.getPurchaseDate())
                .expiryDate(entity.getExpiryDate())
                .vendor(entity.getVendor())
                .supplier(entity.getSupplier())
                .version(entity.getVersion())
                .numberOfSeats(entity.getNumberOfSeats())
                .price(entity.getPrice())
                .currency(entity.getCurrency())
                .installationPath(entity.getInstallationPath())
                .assignedTo(entity.getAssignedTo())
                .assignedEmail(entity.getAssignedEmail())
                .department(entity.getDepartment())
                .station(entity.getStation())
                .description(entity.getDescription())
                .build();
    }

    private void updateEntityFromDto(License entity, LicenseDto dto) {
        if (dto.getLicenseName() != null) {
            entity.setLicenseName(dto.getLicenseName());
        }
        if (dto.getLicenseType() != null) {
            entity.setLicenseType(dto.getLicenseType());
        }
        if (dto.getLicenseKey() != null) {
            entity.setLicenseKey(dto.getLicenseKey());
        }
        if (dto.getStatus() != null) {
            entity.setStatus(dto.getStatus());
        }
        if (dto.getPurchaseDate() != null) {
            entity.setPurchaseDate(dto.getPurchaseDate());
        }
        if (dto.getExpiryDate() != null) {
            entity.setExpiryDate(dto.getExpiryDate());
        }
        if (dto.getVendor() != null) {
            entity.setVendor(dto.getVendor());
        }
        if (dto.getSupplier() != null) {
            entity.setSupplier(dto.getSupplier());
        }
        if (dto.getVersion() != null) {
            entity.setVersion(dto.getVersion());
        }
        if (dto.getNumberOfSeats() != null) {
            entity.setNumberOfSeats(dto.getNumberOfSeats());
        }
        if (dto.getPrice() != null) {
            entity.setPrice(dto.getPrice());
        }
        if (dto.getCurrency() != null) {
            entity.setCurrency(dto.getCurrency());
        }
        if (dto.getInstallationPath() != null) {
            entity.setInstallationPath(dto.getInstallationPath());
        }
        if (dto.getAssignedTo() != null) {
            entity.setAssignedTo(dto.getAssignedTo());
        }
        if (dto.getAssignedEmail() != null) {
            entity.setAssignedEmail(dto.getAssignedEmail());
        }
        if (dto.getDepartment() != null) {
            entity.setDepartment(dto.getDepartment());
        }
        if (dto.getStation() != null) {
            entity.setStation(dto.getStation());
        }
        if (dto.getDescription() != null) {
            entity.setDescription(dto.getDescription());
        }
    }
}

