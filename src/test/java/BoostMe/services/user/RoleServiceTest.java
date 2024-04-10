package BoostMe.services.user;

import BoostMe.entities.user.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class RoleServiceTest {

    @Autowired
    private RoleService roleService;

    @Test
    void testGetUserRole_RoleFound() {

        // Проверяем, что метод getUserRole() возвращает ожидаемую роль
        Role resultRole = roleService.getUserRole();
        assertEquals("USER", resultRole.getName());
    }

}