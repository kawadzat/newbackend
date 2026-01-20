package io.getarrays.securecapita.licence;

import java.util.List;

public interface LicenseService {
    LicenseDto create(LicenseDto licenseDto);
    LicenseDto update(Long id, LicenseDto licenseDto);
    LicenseDto getById(Long id);
    List<LicenseDto> getAll();
    void delete(Long id);
    LicenseDto createAndAssignToLaptop(Long laptopId, LicenseDto licenseDto);
    List<LicenseDto> getLicensesByLaptop(Long laptopId);
    void removeLicenseFromLaptop(Long laptopId, Long licenseId);
    List<LicenseWithLaptopDetailsDto> getAllWithLaptopDetails();
}

