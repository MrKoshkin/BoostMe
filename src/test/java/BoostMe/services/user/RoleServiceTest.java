package BoostMe.services.user;

import BoostMe.entities.user.Role;
import BoostMe.repositories.user.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUserRole_RoleFound() {
        Role userRole = new Role();
        userRole.setName("ROLE_USER");
        when(roleRepository.findByName(Role.USER)).thenReturn(Optional.of(userRole));

        assertEquals("ROLE_USER", roleService.getUserRole().getName());
    }

    @Test
    void testGetUserRole_RoleNotFound() {
        when(roleRepository.findByName(Role.USER)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> roleService.getUserRole());
    }

}