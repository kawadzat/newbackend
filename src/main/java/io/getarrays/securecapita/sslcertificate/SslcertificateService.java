package io.getarrays.securecapita.sslcertificate;

import java.util.List;

public interface SslcertificateService {
    SslcertificateDto create(SslcertificateDto sslcertificateDto);
    SslcertificateDto update(Long id, SslcertificateDto sslcertificateDto);
    SslcertificateDto getById(Long id);
    List<SslcertificateDto> getAll();
    void delete(Long id);
    List<SslcertificateDto> getExpiringSoon(int days);
    List<SslcertificateDto> searchByDomainName(String domainName);
    List<SslcertificateDto> getByVendor(String vendor);
}










