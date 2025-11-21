package com.comfandi.korlon.repositories;

import com.comfandi.korlon.entities.CourseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<CourseEntity,Long> {

    @Query(value = "SELECT c FROM CourseEntity c WHERE c.name = :courseName AND c.modality.name = :modalityName")
    public CourseEntity findCourseByNameAndModality(String courseName, String modalityName);
}
