package com.example.demo.domain.entity.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserDAO userDAO;

    public User getUserRecord(Integer userId) {
        return userDAO.getUserRecord(userId);
    }
}
