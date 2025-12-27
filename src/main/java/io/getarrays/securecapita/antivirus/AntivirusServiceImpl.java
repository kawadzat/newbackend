package io.getarrays.securecapita.antivirus;

import io.getarrays.securecapita.itinventory.Laptop;
import io.getarrays.securecapita.itinventory.LaptopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AntivirusServiceImpl implements AntivirusService {

    private final AntivirusRepository antivirusRepository;
    private final LaptopRepository laptopRepository;

    @Override
    public Antivirus addAntivirusToLaptop(Long laptopId, Antivirus antivirus) {
        // Check if laptop exists
        Laptop laptop = laptopRepository.findById(laptopId)
                .orElseThrow(() -> new IllegalArgumentException("Laptop not found with id: " + laptopId));

        // Check if antivirus with the same key already exists for this laptop
        if (antivirusRepository.existsByKeyAndLaptopId(antivirus.getKey(), laptopId)) {
            throw new IllegalArgumentException("This antivirus is already added to this laptop. Antivirus key must be unique per laptop.");
        }

        // Check if antivirus with the same key exists globally (since key is unique)
        if (antivirusRepository.existsByKey(antivirus.getKey())) {
            throw new IllegalArgumentException("An antivirus with this key already exists. Antivirus key must be unique.");
        }

        // Set the laptop relationship and default values
        antivirus.setLaptop(laptop);
        if (antivirus.getIsInstalled() == null) {
            antivirus.setIsInstalled(true);
        }
        if (antivirus.getStatus() == null) {
            antivirus.setStatus(AntivirusStatus.ACTIVE);
        }

        return antivirusRepository.save(antivirus);
    }

    @Override
    public List<Antivirus> getAntivirusByLaptop(Long laptopId) {
        // Check if laptop exists
        if (!laptopRepository.existsById(laptopId)) {
            throw new IllegalArgumentException("Laptop not found with id: " + laptopId);
        }
        return antivirusRepository.findByLaptopId(laptopId);
    }

    @Override
    public List<Antivirus> getAll() {
        return antivirusRepository.findAll();
    }

//    @Override
//    public Antivirus create(Antivirus antivirus) {
//        return antivirusRepository.save(antivirus);
//    }
//
//    @Override
//    public Antivirus update(Antivirus antivirus) {
//        if (!antivirusRepository.existsById(antivirus.getId())) {
//            throw new ResourceNotFoundException("Antivirus not found with id: " + antivirus.getId());
//        }
//        return antivirusRepository.save(antivirus);
//    }
//
//    @Override
//    public Antivirus getById(Long id) {
//        return antivirusRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Antivirus not found with id: " + id));
//    }

//    @Override
//    public Antivirus getByKey(String key) {
//        return antivirusRepository.findByKey(key)
//                .orElseThrow(() -> new ResourceNotFoundException("Antivirus not found with key: " + key));
//    }

//    @Override
//    public List<Antivirus> getAll() {
//        return antivirusRepository.findAll();
//    }
//
//    @Override
//    public void delete(Long id) {
//        if (!antivirusRepository.existsById(id)) {
//            throw new ResourceNotFoundException("Antivirus not found with id: " + id);
//        }
//        antivirusRepository.deleteById(id);
//    }
//
//    @Override
//    public Antivirus addAntivirusToLaptop(Long laptopId, Antivirus antivirus) {
//        Laptop laptop = laptopRepository.findById(laptopId)
//                .orElseThrow(() -> new ResourceNotFoundException("Laptop not found with id: " + laptopId));
//
//        antivirus.setLaptop(laptop);
//        antivirus.setIsInstalled(true);
//        antivirus.setStatus(AntivirusStatus.ACTIVE);
//
//        return antivirusRepository.save(antivirus);
//    }
//
//    @Override
//    public List<Antivirus> getAntivirusByLaptop(Long laptopId) {
//        if (!laptopRepository.existsById(laptopId)) {
//            throw new ResourceNotFoundException("Laptop not found with id: " + laptopId);
//        }
//        return antivirusRepository.findByLaptopId(laptopId);
//    }
//
//    @Override
//    public void removeAntivirusFromLaptop(Long laptopId, Long antivirusId) {
//        if (!laptopRepository.existsById(laptopId)) {
//            throw new ResourceNotFoundException("Laptop not found with id: " + laptopId);
//        }
//
//        Antivirus antivirus = antivirusRepository.findById(antivirusId)
//                .orElseThrow(() -> new ResourceNotFoundException("Antivirus not found with id: " + antivirusId));
//
//        if (!antivirus.getLaptop().getId().equals(laptopId)) {
//            throw new IllegalArgumentException("Antivirus does not belong to the specified laptop");
//        }
//
//        antivirusRepository.delete(antivirus);
//    }
//
//    @Override
//    public Antivirus updateAntivirusOnLaptop(Long laptopId, Antivirus antivirus) {
//        if (!laptopRepository.existsById(laptopId)) {
//            throw new ResourceNotFoundException("Laptop not found with id: " + laptopId);
//        }
//
//        Antivirus existingAntivirus = antivirusRepository.findById(antivirus.getId())
//                .orElseThrow(() -> new ResourceNotFoundException("Antivirus not found with id: " + antivirus.getId()));
//
//        if (!existingAntivirus.getLaptop().getId().equals(laptopId)) {
//            throw new IllegalArgumentException("Antivirus does not belong to the specified laptop");
//        }
//
//        // Update fields but keep the laptop relationship
//        existingAntivirus.setName(antivirus.getName());
//        existingAntivirus.setKey(antivirus.getKey());
//        existingAntivirus.setRenewTimeInterval(antivirus.getRenewTimeInterval());
//        existingAntivirus.setVersion(antivirus.getVersion());
//        existingAntivirus.setVendor(antivirus.getVendor());
//        existingAntivirus.setStatus(antivirus.getStatus());
//        existingAntivirus.setIsInstalled(antivirus.getIsInstalled());
//        existingAntivirus.setLicenseExpirationDate(antivirus.getLicenseExpirationDate());
//        existingAntivirus.setLastScanDate(antivirus.getLastScanDate());
//
//        return antivirusRepository.save(existingAntivirus);
//    }
//
//    @Override
//    public List<Antivirus> getByName(String name) {
//        return antivirusRepository.findByNameContainingIgnoreCase(name);
//    }
//
//    @Override
//    public List<Antivirus> getByVendor(String vendor) {
//        return antivirusRepository.findByVendor(vendor);
//    }
//
//    @Override
//    public List<Antivirus> getByStatus(AntivirusStatus status) {
//        return antivirusRepository.findByStatus(status);
//    }
//
//    @Override
//    public List<Antivirus> getInstalledAntivirus() {
//        return antivirusRepository.findByIsInstalled(true);
//    }
//
//    @Override
//    public List<Antivirus> getExpiringLicenses(LocalDateTime date) {
//        return antivirusRepository.findExpiringLicenses(date);
//    }
//
//    @Override
//    public List<Antivirus> getOutdatedScans(LocalDateTime date) {
//        return antivirusRepository.findOutdatedScans(date);
//    }
//
//    @Override
//    public boolean isKeyExists(String key) {
//        return antivirusRepository.existsByKey(key);
//    }
}