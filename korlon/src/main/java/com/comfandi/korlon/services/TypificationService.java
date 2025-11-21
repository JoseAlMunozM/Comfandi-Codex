package com.comfandi.korlon.services;

import com.comfandi.korlon.entities.TypificationEntity;
import com.comfandi.korlon.repositories.TypificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TypificationService {

    @Autowired
    private TypificationRepository typificationRepository;


    public List<TypificationEntity> getTypificationByType(String type) {
        return typificationRepository.findByType(type);
    }

}
