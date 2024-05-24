package com.voice.auth.service;


import com.voice.auth.dao.UserAuthRepository;
import com.voice.auth.model.UserAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserAuthService {
    Logger logger = LoggerFactory.getLogger(UserAuthService.class);
    @Autowired
    private UserAuthRepository userAuthRepository;

    public UserAuth saveUserAuth(UserAuth UserAuth) {
        return userAuthRepository.save(UserAuth);
    }


    public List<UserAuth> getAllUserAuth() {
        logger.debug("UserTestService Class");
        return (List<UserAuth>) userAuthRepository.findAll();
    }

    public Optional<UserAuth> getUserAuthByEmail(String userEmail) {
        return userAuthRepository.findByUserEmail(userEmail);
    }
//    public UserAuth getUserAuth(){
//        return userTestRepository.
//    }
}