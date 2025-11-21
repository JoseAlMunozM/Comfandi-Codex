package com.comfandi.korlon.services;

import com.comfandi.korlon.entities.CountableDataEntity;
import com.comfandi.korlon.repositories.CountableDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CountableDataService {

    @Autowired
    private CountableDataRepository countableDataRepository;

    public CountableDataEntity getCountableDataByParameters(String regional, String accountType,int group, int internalGroup) {
        return countableDataRepository.getCountableDataByParameters(regional,accountType, group,internalGroup);
    }
}
