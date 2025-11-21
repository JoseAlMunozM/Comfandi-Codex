package com.comfandi.phobos.service;

import com.comfandi.phobos.entity.UserEntity;
import com.comfandi.phobos.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<UserEntity> getUsersByAccount(Long accountId){
        return userRepository.getUsersByAccountId(accountId);
    }
}
