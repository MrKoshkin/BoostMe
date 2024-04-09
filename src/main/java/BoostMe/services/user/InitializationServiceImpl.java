package BoostMe.services.user;

import BoostMe.entities.user.Role;
import BoostMe.entities.user.User;
import BoostMe.entities.user.UserGroup;
import BoostMe.repositories.user.RoleRepository;
import BoostMe.repositories.user.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Slf4j
@Service
public class InitializationServiceImpl implements InitializationService{

    @Value("${admin.username}")
    private String adminUsername;
    @Value("${admin.password}")
    private String adminPassword;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public InitializationServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    @Override
    public void initializeApp() {
        initializeAdminUser(adminUsername, adminPassword);
        createRoleIfNotExists(Role.ADMIN);
        createRoleIfNotExists(Role.USER);
    }

    // Инициализация админа
    private void initializeAdminUser(String username, String password) {
        if (!userRepository.existsByUsername(username)) {
            User adminUser = new User();
            adminUser.setUsername(username);
            String encodedPassword = passwordEncoder.encode(password);
            adminUser.setPassword(encodedPassword);
            adminUser.setRole(Role.ADMIN);
            adminUser.setHidden(false);
            adminUser.setTimestamp(System.currentTimeMillis());
            adminUser.getUserGroups().addAll(Arrays.asList(UserGroup.values()));

            try {
                userRepository.save(adminUser);
                log.info("Admin has been successfully initialized");
            } catch (Exception e) {
                log.error("Admin initialization error: {}", e.getMessage());
            }
        }
    }

    // Создание роли, если она не существует
    private void createRoleIfNotExists(String roleName) {
        if (!roleRepository.existsByName(roleName)) {
            Role role = new Role();
            role.setName(roleName);
            // Добавьте необходимые настройки для ролей, если это требуется
            roleRepository.save(role);
            log.info("Role '{}' has been successfully initialized", roleName);
        }
    }
}