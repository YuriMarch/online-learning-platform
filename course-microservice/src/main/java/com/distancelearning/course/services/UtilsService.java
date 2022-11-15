package com.distancelearning.course.services;

import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UtilsService {
    String createUrlGetAllUsersInCourse(UUID courseId, Pageable pageable);
    String createUrlGetOneUserById(UUID userId);
}
