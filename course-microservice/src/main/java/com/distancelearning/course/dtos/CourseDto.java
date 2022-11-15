package com.distancelearning.course.dtos;

import com.distancelearning.course.enums.CourseLevel;
import com.distancelearning.course.enums.CourseStatus;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class CourseDto {

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    private String imageUrl;

    @NotNull
    private CourseStatus courseStatus;

    @NotNull
    private CourseLevel courseLevel;

    @NotNull
    private UUID userInstructor;
}
