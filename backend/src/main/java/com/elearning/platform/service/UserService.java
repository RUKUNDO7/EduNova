package com.elearning.platform.service;

import com.elearning.platform.domain.UserAccount;
import com.elearning.platform.domain.UserRole;
import com.elearning.platform.dto.PasswordResetRequest;
import com.elearning.platform.dto.RegisterRequest;
import com.elearning.platform.dto.UserDTO;
import com.elearning.platform.repository.UserAccountRepository;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserAccountRepository userAccountRepository, PasswordEncoder passwordEncoder) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserDTO> listAll() {
        return userAccountRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional
    public UserDTO register(RegisterRequest request) {
        if (userAccountRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }
        UserAccount user = new UserAccount(request.getName(), request.getEmail(),
                passwordEncoder.encode(request.getPassword()), request.getRole());
        return toDTO(userAccountRepository.save(user));
    }

    @Transactional
    public UserDTO resetPassword(PasswordResetRequest request) {
        UserAccount user = userAccountRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        return toDTO(userAccountRepository.save(user));
    }


    public UserAccount findOne(Long id) {
        return userAccountRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }


    public UserAccount createOrGetStudent(String name, String email) {
        return userAccountRepository.findByEmail(email)
                .orElseGet(() -> userAccountRepository.save(new UserAccount(name, email, passwordEncoder.encode("changeme"), UserRole.STUDENT)));
    }

    private UserDTO toDTO(UserAccount user) {
        return new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }
}
