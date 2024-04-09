package BoostMe.services.user;

import BoostMe.api.rest.RegistrationUserDto;
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

import java.util.Optional;
import java.util.stream.Collectors;

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
                user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList())
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
        user.addRole(roleService.getUserRole());

        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException ex) {
            String errorMessage = "Ошибка сохранения пользователя: " + ex.getMessage();
            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage, ex);
        }
    }

}