package BoostMe.services.user;

import BoostMe.api.JwtRequest;
import BoostMe.api.JwtResponse;
import BoostMe.dtos.RegistrationUserDto;
import BoostMe.dtos.UserDto;
import BoostMe.entities.user.User;
import BoostMe.utils.JwtTokenUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class AuthServiceTest {

    @Autowired
    private AuthServiceImpl authService;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtTokenUtils jwtTokenUtils;

    @MockBean
    private AuthenticationManager authenticationManager;

    JwtRequest authRequest;

    @BeforeEach
    void setUp() {
        authRequest = new JwtRequest();
        authRequest.setUsername("testUser");
        authRequest.setPassword("testPassword");
    }

    @Test
    void testCreateAuthToken_SuccessfulAuthentication() {
        UserDetails userDetails = mock(UserDetails.class);
        String token = "testToken";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(userService.loadUserByUsername("testUser")).thenReturn(userDetails);
        when(jwtTokenUtils.generateToken(userDetails)).thenReturn(token);

        // Act
        ResponseEntity<?> response = authService.createAuthToken(authRequest);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof JwtResponse);
        assertEquals(token, ((JwtResponse) response.getBody()).getToken());
    }

    @Test
    void testCreateAuthToken_AuthenticationFailed() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new BadCredentialsException(""));

        // Act
        ResponseEntity<?> response = authService.createAuthToken(authRequest);

        // Assert
        assertNotNull(response);
        assertEquals(401, response.getStatusCodeValue());
    }

    @Test
    void testCreateNewUser_UniqueUsernameAndEmail() {
        // Arrange
        RegistrationUserDto registrationUserDto = new RegistrationUserDto();
        registrationUserDto.setUsername("newUser");
        registrationUserDto.setEmail("test@example.com");
        registrationUserDto.setPassword("password");
        registrationUserDto.setConfirmPassword("password");

        User newUser = new User();
        newUser.setUsername(registrationUserDto.getUsername()); // Установка имени пользователя
        when(userService.findByUsername("newUser")).thenReturn(Optional.empty());
        when(userService.createNewUser(registrationUserDto)).thenReturn(newUser); // Возвращение фиктивного пользователя

        // Act
        ResponseEntity<?> response = authService.createNewUser(registrationUserDto);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof UserDto);
        assertEquals("newUser", ((UserDto) response.getBody()).getUsername());
    }

    @Test
    void testCreateNewUser_NonUniqueUsername() {
        // Arrange
        RegistrationUserDto registrationUserDto = new RegistrationUserDto();
        registrationUserDto.setUsername("existingUser");
        registrationUserDto.setEmail("test@example.com");
        registrationUserDto.setPassword("password");

        when(userService.findByUsername("existingUser")).thenReturn(Optional.of(new User()));

        // Act
        ResponseEntity<?> response = authService.createNewUser(registrationUserDto);

        // Assert
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

}