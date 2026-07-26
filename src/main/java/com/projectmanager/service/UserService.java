package com.projectmanager.service;

import com.projectmanager.dto.UserRegistrationDto;
import com.projectmanager.model.User;

public interface UserService {
    User registerNewUserAccount(UserRegistrationDto registrationDto);
    boolean emailExists(String email);
}
