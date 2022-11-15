package com.distancelearning.course.clients;

import com.distancelearning.course.dtos.CourseDto;
import com.distancelearning.course.dtos.CourseUserDto;
import com.distancelearning.course.dtos.ResponsePageDto;
import com.distancelearning.course.dtos.UserDto;
import com.distancelearning.course.services.UtilsService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Component
@Log4j2
public class AuthUserClient {

    @Value("${distancelearning.api.url.authuser}")
    String REQUEST_URI_AUTHUSER;
    @Autowired
    RestTemplate restTemplate;

    @Autowired
    UtilsService utilsService;

    public Page<UserDto> getAllUsersInCourse(UUID courseId, Pageable pageable) {
        List<UserDto> searchResult = null;
        String url = utilsService.createUrlGetAllUsersInCourse(courseId, pageable);

        log.debug("Request URL: {}", url);
        log.info("Request URL: {}", url);
        ResponseEntity<ResponsePageDto<UserDto>> result = null;
        try {
            ParameterizedTypeReference<ResponsePageDto<UserDto>> responseType =
                    new ParameterizedTypeReference<ResponsePageDto<UserDto>>() {};
            result = restTemplate.exchange(url, HttpMethod.GET, null, responseType);
            searchResult = result.getBody().getContent();

            log.debug("Response number of elements: {}", searchResult.size());
        } catch (HttpStatusCodeException e) {

            log.error("Error request /courses {}", e);
        }
        log.info("Ending request /users courseId {}", courseId);
        return result.getBody();
    }

    public ResponseEntity<UserDto> getOneUserById(UUID userId){
        String url = utilsService.createUrlGetOneUserById(userId);
        return restTemplate.exchange(url, HttpMethod.GET, null, UserDto.class);
    }

    public void postSubscriptionUserInCourse(UUID courseId, UUID userId) {
        String url = REQUEST_URI_AUTHUSER + "/users/" + userId + "/courses/subscription";
        var courseUserDto = new CourseUserDto();
        courseUserDto.setUserId(userId);
        courseUserDto.setCourseId(courseId);
        restTemplate.postForObject(url, courseUserDto, String.class);
    }

    public void deleteCourseInAuthUser(UUID courseId) {
        String url = REQUEST_URI_AUTHUSER + "/users/courses/" + courseId;
        restTemplate.exchange(url, HttpMethod.DELETE, null, String.class);
    }
}
