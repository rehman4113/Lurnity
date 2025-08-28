package com.app.lurnityBackend.service;

import com.app.lurnityBackend.dto.CourseRequestDto;
import com.app.lurnityBackend.dto.CourseResponseDto;
import com.app.lurnityBackend.model.Course;
import com.app.lurnityBackend.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    // Create new course
    public CourseResponseDto createCourse(CourseRequestDto request) {
        Course course = new Course();
        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setPrice(request.getPrice());
        course.setContentUrl(request.getContentUrl());

        Course saved = courseRepository.save(course);

        return mapToDto(saved);
    }

    // Get all courses
    public List<CourseResponseDto> getAllCourses() {
        return courseRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // Get course by ID
    public CourseResponseDto getCourseById(String id) {
        Optional<Course> courseOpt = courseRepository.findById(id);
        return courseOpt.map(this::mapToDto)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));
    }

    // Delete course
    public void deleteCourse(String id) {
        if (!courseRepository.existsById(id)) {
            throw new RuntimeException("Course not found with id: " + id);
        }
        courseRepository.deleteById(id);
    }

    // 🔄 Mapper
    private CourseResponseDto mapToDto(Course course) {
        return new CourseResponseDto(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getPrice(),
                course.getContentUrl()
        );
    }
}
