package com.distancelearning.authuser.services;

import com.distancelearning.authuser.models.User;
import com.distancelearning.authuser.models.UserCourseModel;

import java.util.UUID;

public interface UserCourseService {
    boolean existsByUserAndCourseId(User user, UUID courseId);

    UserCourseModel save(UserCourseModel userCourseModel);

    boolean existsByCourseId(UUID courseId);

    void deleteUserCourseByCourse(UUID courseId);
}
