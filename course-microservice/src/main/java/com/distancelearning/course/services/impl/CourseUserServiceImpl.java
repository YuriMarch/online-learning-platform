package com.distancelearning.course.services.impl;

import com.distancelearning.course.clients.AuthUserClient;
import com.distancelearning.course.models.CourseModel;
import com.distancelearning.course.models.CourseUserModel;
import com.distancelearning.course.repositories.CourseUserRepository;
import com.distancelearning.course.services.CourseUserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@AllArgsConstructor
@Service
public class CourseUserServiceImpl implements CourseUserService {

    private final CourseUserRepository courseUserRepository;

    private final AuthUserClient authUserClient;

    @Override
    public boolean existsByCourseAndUserId(CourseModel courseModel, UUID userId) {
        return courseUserRepository.existsByCourseAndUserId(courseModel, userId);
    }

    @Override
    public CourseUserModel save(CourseUserModel courseUserModel) {
        return courseUserRepository.save(courseUserModel);
    }

    @Transactional //guarantees that the operations are successful. If an error occurs, it will cancel the operation to avoid data mismatch
    @Override
    public CourseUserModel saveAndSendSubscriptionUserInCourse(CourseUserModel courseUserModel) {
        courseUserModel = courseUserRepository.save(courseUserModel);
        authUserClient.postSubscriptionUserInCourse(courseUserModel.getCourse().getCourseId(), courseUserModel.getUserId());
        return courseUserModel;
    }

    @Override
    public boolean existsByUserId(UUID userId) {
        return courseUserRepository.existsByUserId(userId);
    }

    @Transactional
    @Override
    public void deleteCourseUserByUser(UUID userId) {
        courseUserRepository.deleteAllByUserId(userId);
    }
}
