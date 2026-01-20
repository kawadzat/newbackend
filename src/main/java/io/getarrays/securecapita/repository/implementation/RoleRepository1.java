package io.getarrays.securecapita.repository.implementation;

import io.getarrays.securecapita.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RoleRepository1 extends JpaRepository<Role,Long> {
    @Query("SELECT r FROM Role r WHERE TRIM(r.name) = :role ORDER BY r.id")
    List<Role> findByRoleNameList(String role);
    
    default Optional<Role> findByRoleName(String role) {
        List<Role> roles = findByRoleNameList(role);
        return roles.isEmpty() ? Optional.empty() : Optional.of(roles.get(0));
    }
}
