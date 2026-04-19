package com.ott.platform.service;

import com.ott.platform.dto.UserRegistrationDTO;
import com.ott.platform.model.User;
import java.util.List;

/**
 * SRP – user management only.
 * ISP – only methods relevant to user operations exposed here.
 */
public interface UserService {
    User register(UserRegistrationDTO dto);
    User login(String email, String password);
    User findById(Long id);
    List<User> findAll();
    void deleteUser(Long id);
}
