package BoostMe.api.controllers;

import BoostMe.api.JwtRequest;
import BoostMe.dtos.RegistrationUserDto;
import BoostMe.entities.user.Role;
import BoostMe.services.user.UserService;
import BoostMe.utils.JwtTokenUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.util.AssertionErrors.fail;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenUtils jwtTokenUtils;

    @Value("${admin.username}")
    private String adminUsername;
    @Value("${admin.password}")
    private String adminPassword;

    @Test
    public void testCreateAuthToken_ValidCredentials_ReturnsToken() {
        JwtRequest authRequest = new JwtRequest();
        authRequest.setUsername(adminUsername);
        authRequest.setPassword(adminPassword);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity("/auth", authRequest, String.class);

        // Проверяем, что запрос завершился успешно (Статус 200)
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Проверяем токен
        String responseBody = responseEntity.getBody();
        String token = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            token = jsonNode.get("token").asText(); // Извлечение токена
        } catch (Exception e) {
            fail("Failed to parse JSON response: " + e.getMessage());
        }

        assertEquals(authRequest.getUsername(), jwtTokenUtils.getUsername(token));
        assertEquals(Role.ADMIN, jwtTokenUtils.getRoles(token).getFirst());
    }

    @Test
    public void testCreateAuthToken_AccessDenied_ReturnsForbidden() {

        ResponseEntity<String> responseEntity = restTemplate.postForEntity("/api/protected", null, String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
    }

    @Test
    public void testCreateNewUser_InvalidUserData_ReturnsBadRequest() {
        // Отправляем POST запрос с невалидными данными нового пользователя (например, без имени пользователя)
        RegistrationUserDto newUserDto = new RegistrationUserDto();
        newUserDto.setPassword("invalidPassword");

        ResponseEntity<String> responseEntity = restTemplate.postForEntity("/registration", newUserDto, String.class);

        // Проверяем, что запрос завершился с ошибкой неверного запроса (Статус 400)
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void testCreateNewUser_ValidUserData_ReturnsOk() {
        // Отправляем POST запрос с валидными данными нового пользователя
        RegistrationUserDto newUserDto = new RegistrationUserDto();
        newUserDto.setUsername("testUser");
        newUserDto.setPassword("testPassword");
        newUserDto.setConfirmPassword("testPassword");
        newUserDto.setEmail("testEmail");

        ResponseEntity<String> responseEntity = restTemplate.postForEntity("/registration", newUserDto, String.class);

        // Проверяем, что запрос завершился успешно (HTTP статус 200 OK)
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Удалить пользователя после выполнения теста
        userService.deleteUserByUsername("testUser");
    }
}