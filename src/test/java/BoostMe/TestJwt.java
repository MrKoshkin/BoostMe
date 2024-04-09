package BoostMe;

import BoostMe.utils.JwtTokenUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class TestJwt {

    @Autowired
    private JwtTokenUtils jwtTokenUtils;

    @Test
    void generateToken_ValidUserDetails_ReturnsToken() {
        // Создаем объект UserDetails для тестирования
        UserDetails userDetails = new User(
                "kosh",
                "kosh",
                Collections.singletonList(new SimpleGrantedAuthority("USER"))
        );

        // Создаем объект JwtTokenUtils для тестирования
        JwtTokenUtils jwtTokenUtils = new JwtTokenUtils();


        // Генерируем токен
        String token = jwtTokenUtils.generateToken(userDetails);

        assertNotNull(token);
        assertTrue(token.length() > 0);
    }
}
