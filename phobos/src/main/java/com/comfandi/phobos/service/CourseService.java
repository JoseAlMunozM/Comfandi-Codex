package com.comfandi.phobos.service;

import com.comfandi.phobos.entity.CourseEntity;
import com.comfandi.phobos.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    public CourseEntity findCourseByNames(String programName, String modality,Integer year){
        return courseRepository.findCourseByNameAndModality(programName,modality,year);
    }
}
