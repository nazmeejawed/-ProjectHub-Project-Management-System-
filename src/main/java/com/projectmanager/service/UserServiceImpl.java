package com.projectmanager.service;

import com.projectmanager.dto.UserRegistrationDto;
import com.projectmanager.model.Role;
import com.projectmanager.model.User;
import com.projectmanager.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User registerNewUserAccount(UserRegistrationDto registrationDto) {
        if (emailExists(registrationDto.getEmail())) {
            throw new IllegalArgumentException("There is an account with that email address: " + registrationDto.getEmail());
        }

        User user = new User();
        user.setFullName(registrationDto.getFullName());
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        // Default role for new signups
        user.setRole(Role.DEVELOPER);
        user.setEnabled(true);

        return userRepository.save(user);
    }

    @Override
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }
}
