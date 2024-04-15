package BoostMe.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class JwtTokenUtilsTest {

    @Autowired
    private JwtTokenUtils jwtTokenUtils;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        // Создаем объект UserDetails для тестирования
        userDetails = new User(
                "kosh",
                "kosh",
                Collections.singletonList(new SimpleGrantedAuthority("USER"))
        );
    }

    @Test
    void generateToken_ValidUserDetails_ReturnsToken() {
        // Генерируем токен
        String token = jwtTokenUtils.generateToken(userDetails);

        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    void getUsername_ValidToken_ReturnsUsername() {
        // Генерируем токен на основе userDetails
        String token = jwtTokenUtils.generateToken(userDetails);

        String username = jwtTokenUtils.getUsername(token);

        assertEquals("kosh", username);
    }

    @Test
    void getRoles_ValidToken_ReturnsRoles() {
        // Генерируем токен на основе userDetails
        String token = jwtTokenUtils.generateToken(userDetails);

        List<String> roles = jwtTokenUtils.getRoles(token);

        assertEquals(List.of("USER"), roles);
    }
}
