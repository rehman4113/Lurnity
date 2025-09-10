package com.app.lurnityBackend.controller;

import com.app.lurnityBackend.dto.CourseRequestDto;
import com.app.lurnityBackend.dto.CourseResponseDto;
import com.app.lurnityBackend.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    // ✅ List all courses (any authenticated user)
    @GetMapping
    public ResponseEntity<List<CourseResponseDto>> getAllCourses(Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        return ResponseEntity.ok(courseService.getAllCourses(isAdmin));
    }

    // ✅ Get course details
    @GetMapping("/{id}")
    public ResponseEntity<CourseResponseDto> getCourseById(
            @PathVariable String id,
            Authentication authentication) {

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        return ResponseEntity.ok(courseService.getCourseById(id, isAdmin));
    }

    // ✅ Add new course (only ADMIN)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CourseResponseDto> createCourse(@RequestBody CourseRequestDto request) {
        return ResponseEntity.ok(courseService.createCourse(request));
    }

    // ✅ Delete course (only ADMIN)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCourse(@PathVariable String id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }
}



//package com.app.lurnityBackend.controller;
//
//import com.app.lurnityBackend.dto.CourseRequestDto;
//import com.app.lurnityBackend.dto.CourseResponseDto;
//import com.app.lurnityBackend.service.CourseService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/courses")
//@RequiredArgsConstructor
//public class CourseController {
//
//    private final CourseService courseService;
//
//    // ✅ List all courses (any authenticated user)
//    @GetMapping
//    public ResponseEntity<List<CourseResponseDto>> getAllCourses() {
//        return ResponseEntity.ok(courseService.getAllCourses());
//    }
//
//    // ✅ Get course details
//    @GetMapping("/{id}")
//    public ResponseEntity<CourseResponseDto> getCourseById(@PathVariable String id) {
//        return ResponseEntity.ok(courseService.getCourseById(id));
//    }
//
//    // ✅ Add new course (only ADMIN)
//    @PostMapping
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<CourseResponseDto> createCourse(@RequestBody CourseRequestDto request) {
//        return ResponseEntity.ok(courseService.createCourse(request));
//    }
//
//    // ✅ Delete course (only ADMIN)
//    @DeleteMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<Void> deleteCourse(@PathVariable String id) {
//        courseService.deleteCourse(id);
//        return ResponseEntity.noContent().build();
//    }
//}
