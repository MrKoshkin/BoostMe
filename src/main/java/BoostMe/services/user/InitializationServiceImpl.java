package BoostMe.services.user;

import BoostMe.entities.user.Role;
import BoostMe.entities.user.User;
import BoostMe.entities.user.UserGroup;
import BoostMe.repositories.user.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class InitializationServiceImpl implements InitializationService{

    @Value("${admin.username}")
    private String adminUsername;
    @Value("${admin.password}")
    private String adminPassword;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public InitializationServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    @Override
    public void initializeAdminUsers() {
        initializeAdminUser(adminUsername, adminPassword);
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

            userRepository.save(adminUser);
        }
    }
}