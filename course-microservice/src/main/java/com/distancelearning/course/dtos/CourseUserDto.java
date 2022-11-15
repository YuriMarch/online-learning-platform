package com.distancelearning.course.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CourseUserDto {

    private UUID courseId;
    private UUID userId;
}
