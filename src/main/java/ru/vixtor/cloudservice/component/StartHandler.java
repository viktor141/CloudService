package ru.vixtor.cloudservice.component;

import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.vixtor.cloudservice.entity.User;
import ru.vixtor.cloudservice.repository.UserRepository;

@Component
@AllArgsConstructor
public class StartHandler implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        userRepository.save(User.builder()
                .login("user1@netology.ru")
                .password(passwordEncoder.encode("123"))
                .build());
        userRepository.save(User.builder()
                .login("user2@netology.ru")
                .password(passwordEncoder.encode("456"))
                .build());
        userRepository.save(User.builder()
                .login("user3@netology.ru")
                .password(passwordEncoder.encode("789"))
                .build());
    }
}
