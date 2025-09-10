package com.app.lurnityBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data   // Generates getters, setters, toString, equals, hashCode
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponseDto {
    private String id;          // MongoDB generated ID
    private String title;
    private String description;
    private Double price;
    private String contentUrl;
    private String imageUrl;
}
