package com.comfandi.phobos.service;

import com.comfandi.phobos.entity.RegionalEntity;
import com.comfandi.phobos.repository.RegionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegionalService {

    @Autowired
    private RegionalRepository regionalRepository;

    public List<RegionalEntity> getAllRegionals(){
        return regionalRepository.findAll();
    }

    public RegionalEntity findByName(List<RegionalEntity> regionalEntityList, String regional){
        return regionalEntityList.stream().filter(x-> x.getName().equals(regional)).findFirst().orElse(null);
    }
}
