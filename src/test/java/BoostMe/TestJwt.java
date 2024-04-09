package BoostMe;

import BoostMe.utils.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Duration;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RequiredArgsConstructor
public class TestJwt {

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
        jwtTokenUtils.setSecret("testSecret");
        jwtTokenUtils.setJwtLifetime(Duration.ofHours(1));


        // Генерируем токен
        String token = jwtTokenUtils.generateToken(userDetails);

        assertNotNull(token);
        assertTrue(token.length() > 0);
    }
}
