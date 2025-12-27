package io.getarrays.securecapita.antivirus;

import java.util.List;

public interface AntivirusService {
    Antivirus addAntivirusToLaptop(Long laptopId, Antivirus antivirus);
    List<Antivirus> getAntivirusByLaptop(Long laptopId);
    List<Antivirus> getAll();
    // Basic CRUD operations
//    Antivirus create(Antivirus antivirus);
//    Antivirus update(Antivirus antivirus);
//    Antivirus getById(Long id);
//    Antivirus getByKey(String key);
//    void delete(Long id);
//    
//    // Laptop-related methods
//    Antivirus addAntivirusToLaptop(Long laptopId, Antivirus antivirus);
//    void removeAntivirusFromLaptop(Long laptopId, Long antivirusId);
//    Antivirus updateAntivirusOnLaptop(Long laptopId, Antivirus antivirus);
//    
//    // Search and filter methods
//    List<Antivirus> getByName(String name);
//    List<Antivirus> getByVendor(String vendor);
//    List<Antivirus> getByStatus(AntivirusStatus status);
//    List<Antivirus> getInstalledAntivirus();
//    List<Antivirus> getExpiringLicenses(LocalDateTime date);
//    List<Antivirus> getOutdatedScans(LocalDateTime date);
//    boolean isKeyExists(String key);
} 