package com.app.lurnityBackend.repository;

import com.app.lurnityBackend.model.Course;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends MongoRepository<Course, String> {
    // You can add custom queries here if needed
}
