package io.getarrays.securecapita.antivirus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AntivirusRepository extends JpaRepository<Antivirus, Long> {
    
    Optional<Antivirus> findByKey(String key);
    
    boolean existsByKey(String key);
    
    @Query("SELECT a FROM Antivirus a WHERE a.key = :key AND a.laptop.id = :laptopId")
    Optional<Antivirus> findByKeyAndLaptopId(@Param("key") String key, @Param("laptopId") Long laptopId);
    
    boolean existsByKeyAndLaptopId(String key, Long laptopId);
    
    @Query("SELECT a FROM Antivirus a WHERE a.laptop.id = :laptopId")
    List<Antivirus> findByLaptopId(@Param("laptopId") Long laptopId);
} 