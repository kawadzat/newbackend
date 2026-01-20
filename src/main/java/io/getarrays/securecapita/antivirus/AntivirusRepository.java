package io.getarrays.securecapita.antivirus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AntivirusRepository extends JpaRepository<Antivirus, Long> {

} 