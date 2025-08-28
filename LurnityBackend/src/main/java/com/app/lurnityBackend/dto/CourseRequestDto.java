package com.app.lurnityBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data   // generates getters, setters, toString, equals, hashCode
@NoArgsConstructor
@AllArgsConstructor
public class CourseRequestDto {
    private String title;
    private String description;
    private Double price;
    private String contentUrl;
}
