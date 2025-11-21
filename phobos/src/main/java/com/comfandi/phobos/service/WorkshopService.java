package com.comfandi.phobos.service;

import com.comfandi.phobos.entity.WorkshopEntity;
import com.comfandi.phobos.repository.WorkshopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.Optional;

@Service
public class WorkshopService {

    @Autowired
    private WorkshopRepository workshopRepository;

    public Optional<WorkshopEntity> getWorkshopByName(String name,Integer year){
        return workshopRepository.getByName(name,year);
    }
}
