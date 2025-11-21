package com.comfandi.korlon.services;

import com.comfandi.korlon.entities.CourseEntity;
import com.comfandi.korlon.repositories.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    public Optional<CourseEntity> findCourse(Long id){
        return courseRepository.findById(id);
    }
}
