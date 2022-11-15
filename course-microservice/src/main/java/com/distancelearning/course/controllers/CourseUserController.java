package com.distancelearning.course.controllers;

import com.distancelearning.course.clients.AuthUserClient;
import com.distancelearning.course.dtos.SubscriptionDto;
import com.distancelearning.course.dtos.UserDto;
import com.distancelearning.course.enums.UserStatus;
import com.distancelearning.course.models.CourseModel;
import com.distancelearning.course.models.CourseUserModel;
import com.distancelearning.course.services.CourseService;
import com.distancelearning.course.services.CourseUserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;

import javax.validation.Valid;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@Log4j2
public class CourseUserController {

    @Autowired
    AuthUserClient authUserClient;

    @Autowired
    CourseService courseService;

    @Autowired
    CourseUserService courseUserService;

    @GetMapping("/courses/{courseId}/users")
    public ResponseEntity<Object> getAllUsersInCourse(@PathVariable UUID courseId,
                                                             @PageableDefault(page = 0, size = 5, sort = "userId",
                                                               direction = Sort.Direction.ASC) Pageable pageable){
        Optional<CourseModel> courseModelOptional = courseService.findById(courseId);
        if(courseModelOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found.");
        }
        return ResponseEntity.status(HttpStatus.OK).body(authUserClient.getAllUsersInCourse(courseId, pageable));
    }

    @PostMapping("/courses/{courseId}/users/subscription")
    public ResponseEntity<Object> saveSubscriptionUserInCourse(@PathVariable UUID courseId,
                                                               @RequestBody @Valid SubscriptionDto subscriptionDto){
        ResponseEntity<UserDto> responseUser;
        Optional<CourseModel> courseModelOptional = courseService.findById(courseId);

        if(courseModelOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found.");
        }

        if (courseUserService.existsByCourseAndUserId(courseModelOptional.get(), subscriptionDto.getUserId())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: user is already subscribed to this course.");
        }

        try {
            responseUser = authUserClient.getOneUserById(subscriptionDto.getUserId());
            if (responseUser.getBody().getUserStatus().equals(UserStatus.BLOCKED)){
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: user is blocked.");
            }
        } catch (HttpStatusCodeException e){
            if (e.getStatusCode().equals(HttpStatus.NOT_FOUND)){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: user not found.");
            }
        }

        CourseUserModel courseUserModel = courseUserService.saveAndSendSubscriptionUserInCourse(courseModelOptional.get().convertToCourseUserModel(subscriptionDto.getUserId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(courseUserModel);
    }

    @DeleteMapping("/courses/users/{userId}")
    public ResponseEntity<Object> deleteCourseUserByUser(@PathVariable(value = "userId") UUID userId){
        if (!courseUserService.existsByUserId(userId)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: CourseUser not found.");
        }
        courseUserService.deleteCourseUserByUser(userId);
        return ResponseEntity.status(HttpStatus.OK).body("CourseUser successfully deleted.");
    }
}
