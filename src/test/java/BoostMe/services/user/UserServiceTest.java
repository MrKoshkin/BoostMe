package BoostMe.services.user;

import BoostMe.api.rest.RegistrationUserDto;
import BoostMe.entities.user.Role;
import BoostMe.entities.user.User;
import BoostMe.repositories.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleService roleService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoadUserByUsername_UserFound() {
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("testPassword");
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));

        assertEquals("testUser", userService.loadUserByUsername("testUser").getUsername());
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        when(userRepository.findByUsername("nonExistingUser")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("nonExistingUser"));
    }

    @Test
    void testCreateNewUser_UniqueUsernameAndEmail() {
        RegistrationUserDto registrationUserDto = new RegistrationUserDto();
        registrationUserDto.setUsername("testUser");
        registrationUserDto.setEmail("test@example.com");
        registrationUserDto.setPassword("testPassword");

        when(userRepository.existsByUsername("testUser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);

        User createdUser = userService.createNewUser(registrationUserDto);

        assertEquals("testUser", createdUser.getUsername());
        assertEquals("test@example.com", createdUser.getEmail());
    }

}