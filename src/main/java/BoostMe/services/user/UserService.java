package BoostMe.services.user;

import BoostMe.dtos.RegistrationUserDto;
import BoostMe.entities.user.User;
import BoostMe.repositories.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@Slf4j
@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(
                String.format("Пользователь '%s' не найден", username)
        ));
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole().getName()))
        );
    }

    @Transactional
    public User createNewUser(RegistrationUserDto registrationUserDto) {
        // Проверка уникальности username
        if (userRepository.existsByUsername(registrationUserDto.getUsername())) {
            String message = "Имя пользователя '" + registrationUserDto.getUsername() + "' уже занято";
            log.warn(message);
            throw new IllegalArgumentException(message);
        }

        // Проверка уникальности email
        if (userRepository.existsByEmail(registrationUserDto.getEmail())) {
            String message = "Электронная почта '" + registrationUserDto.getEmail() + "' уже используется";
            log.warn(message);
            throw new IllegalArgumentException(message);
        }

        // Создание нового пользователя
        User user = new User();
        user.setUsername(registrationUserDto.getUsername());
        user.setEmail(registrationUserDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationUserDto.getPassword()));
        user.setRole(roleService.getUserRole());
        user.setTimestamp(System.currentTimeMillis());

        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            String errorMessage = "Ошибка сохранения пользователя: " + e.getMessage();
            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage, e);
        }
    }

    @Transactional
    public void deleteUserByUsername(String username) {
        if (userRepository.existsByUsername(username)) {
            userRepository.deleteByUsername(username);
            log.info("Пользователь '{}' успешно удален", username);
        } else {
            log.warn("Пользователь '{}' не найден, удаление не выполнено", username);
        }
    }

}