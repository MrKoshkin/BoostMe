package BoostMe.services.user;

import BoostMe.entities.user.Role;
import BoostMe.repositories.user.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    public Role getUserRole() {
        try {
            return roleRepository.findByName(Role.USER).orElseThrow(() -> new RuntimeException("Role not found"));
        } catch (RuntimeException e) {
            log.error("Failed to fetch user role: {}", e.getMessage());
            throw e;
        }
    }
}