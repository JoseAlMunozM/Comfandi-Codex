package com.comfandi.phobos.service;

import com.comfandi.phobos.entity.ModalityEntity;
import com.comfandi.phobos.repository.ModalityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModalityService {

    @Autowired
    private ModalityRepository modalityRepository;

    public List<ModalityEntity> getAllMModality(){
        return modalityRepository.findAll();
    }

    public ModalityEntity findByName(List<ModalityEntity> modalityEntityList, String modality){
        return modalityEntityList.stream().filter(x-> x.getName().equals(modality)).findFirst().orElse(null);
    }
}
