package com.ott.platform.service.impl;

import com.ott.platform.dto.UserRegistrationDTO;
import com.ott.platform.model.User;
import com.ott.platform.pattern.factory.UserFactory;
import com.ott.platform.repository.UserRepository;
import com.ott.platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * SRP  – handles only user registration / authentication.
 * DIP  – depends on UserRepository (interface) and UserFactory.
 * Uses Factory Method pattern to create correct User subtype.
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;
    private final UserFactory    userFactory;

    @Autowired
    public UserServiceImpl(UserRepository userRepo, UserFactory userFactory) {
        this.userRepo    = userRepo;
        this.userFactory = userFactory;
    }

    @Override
    public User register(UserRegistrationDTO dto) {
        if (userRepo.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already registered: " + dto.getEmail());
        }
        // Factory Method Pattern – correct subtype created here
        User user = userFactory.createUser(
                dto.getName(), dto.getEmail(), dto.getPassword(), dto.getUserType());
        return userRepo.save(user);
    }

    @Override
    public User login(String email, String password) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Invalid credentials");
        }
        return user;
    }

    @Override
    public User findById(Long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
    }

    @Override
    public List<User> findAll() {
        return userRepo.findAll();
    }

    @Override
    public void deleteUser(Long id) {
        userRepo.deleteById(id);
    }
}
